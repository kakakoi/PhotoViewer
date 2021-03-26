package com.kakakoi.photoviewer.network

import android.util.Log
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.Photo
import com.kakakoi.photoviewer.data.SmbDirectory
import com.kakakoi.photoviewer.data.SmbStatus
import com.kakakoi.photoviewer.data.Storage
import com.kakakoi.photoviewer.extensions.getNowDate
import com.kakakoi.photoviewer.extensions.mimetype
import jcifs.smb.SmbFile
import java.util.*

class SmbIndex (
    private val application: PhotoViewerApplication,
    private val storage: Storage
) {

    companion object {
        private const val REGEX_IMAGE_FILE = "(?i).*\\.(jpg|png|gif|WebP|BMP|ICO|WBMP|HEIF|HEIC)"
        private val regexImageFile = Regex(REGEX_IMAGE_FILE)

        private var instance: SmbIndex? = null
        private var smb:Smb? = null
        private var parentSmbFile: SmbFile? = null

        fun getInstance(application: PhotoViewerApplication, storage: Storage) = instance ?: synchronized(this) {
            instance ?: SmbIndex(
                application,
                storage
            ).also {
                instance = it
                smb = Smb(application, storage)
            }
        }
    }

    private fun isRunning(): Boolean {
        return parentSmbFile?.let { true } ?: false
    }

    fun finish(){
        if(isRunning()) {
            throw IllegalStateException("already running")
        } else {
            instance = null
        }
    }

    fun deepCreateIndex(): Int {
        if(isRunning()) throw IllegalStateException("already running")
        var countIndex = 0

        smb?.run {
            parentSmbFile = this.connect()//基点ディレクトリ
            var target = parentSmbFile//インデックス対象
            while (target?.isDirectory == true) {
                target?.let {
                    Log.d(Smb.TAG, "deepCreateIndex: ${it.path}")
                    countIndex += createIndex(it)//インデックス実行. インデックス件数更新
                    //次のインデックス対象
                    val nextDirectory = application.smbDirectoryRepository.findWait()
                    if(nextDirectory != null) {
                        val oldPath = it.path
                        val newPath = nextDirectory.path
                        //ループ対策
                        if (oldPath == newPath) {
                            throw IllegalStateException("oldPath == newPath")
                        } else {
                            target = this.connect(newPath)//次のインデックス対象をセット
                        }
                    } else {
                        target = null
                    }
                }
                if (target == null) break
            }
        }
        parentSmbFile?.close()
        parentSmbFile = null
        finish()//TODO simple

        return countIndex
    }

    private fun createIndex(smbFile: SmbFile): Int {
        val startTime = System.currentTimeMillis()
        var countIndex = 0

        // 検索のベースとなるパスを未処理として登録
        initSmbDirectory(smbFile)

        // Index 開始
        val iterator = smbFile.children()
        while (iterator.hasNext()) {
            val resource = iterator.next() as SmbFile
            if (resource.isDirectory()) { //Directoryの場合は未処理として登録
                initSmbDirectory(resource)
                Log.d(Smb.TAG, "createIndex: is Directory ${resource.name}")
            } else if (resource.name.matches(regexImageFile)) {// 画像ファイルの場合はインデックス済みとして登録
                countIndex += 1
                indexCompletePhoto(resource)
                Log.d(Smb.TAG, "createIndex: is Image file ${resource.path}")
            } else {//上記以外は無視
                Log.d(Smb.TAG, "createIndex: Exclusion file ${resource.name}")
            }
        }
        //インデックス完了として更新
        indexCompleteSmbDirectory(smbFile, countIndex)
        return countIndex
    }

    //SMB Directory 初期化. 未処理として登録
    private fun initSmbDirectory(smbFile: SmbFile) {
        application.smbDirectoryRepository.marge(
            SmbDirectory(
                smbFile.path,
                storage.id,
                Calendar.getInstance().getNowDate(),
                Calendar.getInstance().getNowDate(),
                -1,
                -1,
                SmbState.WAIT.name
            )
        )
    }

    //SMB Directory インデックス完了として登録
    private fun indexCompleteSmbDirectory(smbFile: SmbFile, countIndex: Int) {
        application.smbDirectoryRepository.marge(
            SmbDirectory(
                smbFile.path,
                storage.id,
                Calendar.getInstance().getNowDate(),//TODO fix old date
                Calendar.getInstance().getNowDate(),
                -1,//TODO fix loading counter
                countIndex,
                SmbState.INDEX.name
            )
        )
    }

    private fun indexCompletePhoto(smbFile: SmbFile) {
        application.photoRepository.marge(
            Photo(
                0,
                smbFile.name,
                Calendar.getInstance().getNowDate(),
                Calendar.getInstance().getNowDate(),//TODO data time origin
                -1,
                "",
                smbFile.path,
                smbFile.name.mimetype(),
                false
            )
        )
        updateStatus(status = SmbState.INDEX.name, smbFile = smbFile)
    }

    private fun updateStatus(id: String = "", status: String = "", smbFile: SmbFile?) {
        application.smbStatusRepository.update(
            SmbStatus(
                id,
                status,
                smbFile?.path ?: "",
                smbFile?.length() ?: 0
            )
        )
    }
}