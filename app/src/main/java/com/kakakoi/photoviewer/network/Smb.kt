package com.kakakoi.photoviewer.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.Photo
import com.kakakoi.photoviewer.data.SmbDirectory
import com.kakakoi.photoviewer.data.SmbStatus
import com.kakakoi.photoviewer.data.Storage
import com.kakakoi.photoviewer.lib.Event
import jcifs.CIFSContext
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import java.io.InputStream
import java.net.MalformedURLException
import java.util.*


class Smb(val application: PhotoViewerApplication, val storage: Storage) {

    companion object {
        const val TAG = "Smb"
        const val SMB_SCHEME = "smb:\\\\"
        const val REGEX_IMAGE_FILE = "(?i).*\\.(jpg)"
    }

    val onTransit = MutableLiveData<Event<String>>()
    val onProgress = MutableLiveData<Boolean>(false)

    private val cifsContext = login()
    private val basePath = SMB_SCHEME + storage.address + storage.dir
    val info = "smb info: basePath=${basePath}, user=${storage.user}, pass=${storage.pass}"
    private val regexImageFile = Regex(REGEX_IMAGE_FILE)

    private fun login(): CIFSContext{
        val prop = Properties()
        setProperties(prop)

        //接続情報を作成する
        val baseContext = BaseContext(PropertyConfiguration(prop))
        val authenticator = NtlmPasswordAuthenticator(storage.user, storage.pass)
        return baseContext.withCredentials(authenticator)
    }

    private fun setProperties(p: Properties) {
        p.setProperty("jcifs.smb.client.minVersion", "SMB1")
        p.setProperty("jcifs.smb.client.maxVersion", "SMB311")
    }

    fun connect(uncPath: String = basePath): SmbFile? {
        return try {
            val url = uncPath?.replace("\\", "/")
            SmbFile(url, cifsContext)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            null
        }
    }

    fun deepCreateIndex(smbFile: SmbFile) {
        var target = smbFile
        do {
            createIndex(target)
            application.smbDirectoryRepository.findWait()?.let {
                connect(it.path)?.let { next ->
                    target = next
                    Log.d(TAG, "deepCreateIndex: next ${target.path}")
                }
            }
        }while (target != null)
        application.smbStatusRepository.update(SmbStatus(
            "",
            "",
            "",
            0,
            false
        ))
    }

    fun createIndex(smbFile: SmbFile) {
        val startTime = System.currentTimeMillis()
        var count = 0

        // insert base path
        application.smbDirectoryRepository.marge(
            SmbDirectory(
                smbFile.path,
                storage.id,
                getNowDate(),
                getNowDate(),
                -1,
                -1,
                "wait"
            )
        )

        val iterator = smbFile.children()
        while (iterator.hasNext()) {
            val resource = iterator.next() as SmbFile
            if (resource.isDirectory()) {
                //insert directory
                application.smbDirectoryRepository.marge(
                    SmbDirectory(
                        resource.path,
                        storage.id,
                        getNowDate(),
                        getNowDate(),
                        -1,
                        -1,
                        "wait"
                    )
                )
                Log.d(TAG, "createIndex: is Directory ${resource.name}")
            } else if (resource.name.matches(regexImageFile)) {
                //insert image file
                application.photoRepository.marge(
                    Photo(
                        0,
                        resource.name,
                        getNowDate(),
                        getNowDate(),
                        -1,
                        "",
                        resource.path,
                        "JPG",
                        false
                    )
                )
                application.smbStatusRepository.update(SmbStatus(
                    "",
                    "",
                    resource.name,
                    resource.length(),
                    true
                ))
                Log.d(TAG, "createIndex: is Image file ${resource.path}")
            } else {
                Log.d(TAG, "createIndex: Exclusion file ${resource.name}")
            }
        }
        application.smbDirectoryRepository.marge(
            SmbDirectory(
                smbFile.path,
                storage.id,
                getNowDate(),//TODO fix old date
                getNowDate(),
                -1,//TODO fix counter
                -1,//TODO fix counter
                "indexComplete"
            )
        )
    }

    fun copyImageFile(smbFile: SmbFile){
        val startTime = System.currentTimeMillis()

        val bitmapCompressInt = 100
        val inputSize = smbFile.length()
        val inputPath = smbFile.path
        val inputDir = inputPath.replace(basePath, "")
        val outName = inputDir.replace("/", "_")

        val fileOut = application.openFileOutput(outName, Context.MODE_PRIVATE)
        val bitmap = decodeSampledBitmap(smbFile.inputStream, REQUIRED_SIZE, REQUIRED_SIZE)
        val result = bitmap?.compress(Bitmap.CompressFormat.JPEG, bitmapCompressInt, fileOut)
        Log.d(TAG,
            "copyImageFile[${smbFile.path}] local file name[$outName] time is:${(System.currentTimeMillis() - startTime)}")

    }

    val REQUIRED_SIZE = 300

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun decodeSampledBitmap(
        f: InputStream,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        // First decode with inJustDecodeBounds=true to check dimensions
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(f, null, this);

            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            BitmapFactory.decodeStream(f, null, this)
        }
    }

    /**
     * 現在日時をyyyy/MM/dd HH:mm:ss形式で取得する.<br></br>
     */
    fun getNowDate(): String {
        return DateFormat.format("yyyy/MM/dd kk:mm:ss", Calendar.getInstance()) as String
    }
}