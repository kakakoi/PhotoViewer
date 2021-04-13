package com.kakakoi.photoviewer.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import jcifs.smb.SmbFile
import java.io.InputStream
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

val REQUIRED_SIZE = 300
val TAG = "extensions.SmbFileExt"

fun SmbFile.downloadShrinkPhoto(
    context: Context,
    basePath: String
): String{
    Log.d(TAG, "downloadShrinkPhoto: start [${this.path}] ")
    val startTime = System.currentTimeMillis()

    val bitmapCompressInt = 100
    val inputSize = this.length()
    val inputPath = this.path
    Log.d(TAG, "downloadShrinkPhoto: basepath $basePath target file $inputPath")
    val inputDir = inputPath.replace(basePath, "")
    val outName = inputDir.replace("/", "_")

    val fileOut = context.openFileOutput(outName, Context.MODE_PRIVATE)
    val bitmap = this.decodeSampledBitmap(REQUIRED_SIZE, REQUIRED_SIZE)
    val result = bitmap?.compress(Bitmap.CompressFormat.JPEG, bitmapCompressInt, fileOut)
    Log.d(TAG, "downloadShrinkPhoto:  #1 [${this.path}] > [$outName] time:${(System.currentTimeMillis() - startTime)}")

    return outName
}

fun SmbFile.decodeSampledBitmap(
    reqWidth: Int,
    reqHeight: Int
): Bitmap? {
// First decode with inJustDecodeBounds=true to check dimensions
    return BitmapFactory.Options().let {
        var inputStream = this.inputStream//TODO runCatch
        it.inJustDecodeBounds = true
        BitmapFactory.decodeStream(this.inputStream, null, it);

        // Calculate inSampleSize
        it.inSampleSize = calculateInSampleSize(it, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        it.inJustDecodeBounds = false
        val degree = getDegree(inputStream)
        Log.d(TAG, "decodeSampledBitmap: degree=$degree name=${this.name}")

        inputStream.close()
        inputStream = this.inputStream
        //初めのdecodeStreamで
        val bitmap = BitmapFactory.decodeStream(inputStream, null, it)?.let {
            rotateImage(it, degree)
        }
        inputStream.close()

        return bitmap
    }
}

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

fun getDegree(inputStream: InputStream): Float {
    val exif = ExifInterface(inputStream)
    return when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED)) {
        // 正しい方向の場合は回転させない
        1 -> { 0f }
        // 逆向きなので180度回転させる
        3-> { 180f }
        // 左向きの画像になってるので90度回転させる
        6 -> { 90f }
        // 右向きの画像になってるので270度回転させる
        8 -> { 270f }
        else -> { 0f }
    }
}

fun rotateImage(bitmap: Bitmap, degree: Float): Bitmap? {
    if (degree <= 0f) return bitmap
    val matrix = Matrix()
    matrix.postRotate(degree)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun SmbFile.getExifDateTimeOriginal(): Long {
    val exif = ExifInterface(this.inputStream)
    val time = parseDateTime(
        exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL),
        exif.getAttribute(ExifInterface.TAG_SUBSEC_TIME_ORIGINAL)
    )
    return if(time > 0) time else this.date
}

private fun parseDateTime(dateTimeString: String?, subSecs: String?): Long {
    val sFormatter = SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
    sFormatter.timeZone = TimeZone.getTimeZone("UTC")
    if (dateTimeString == null
        || !sNonZeroTimePattern.matcher(dateTimeString).matches()
    ) return -1
    val pos = ParsePosition(0)
    return try {
        // The exif field is in local time. Parsing it as if it is UTC will yield time
        // since 1/1/1970 local time
        val datetime = sFormatter.parse(dateTimeString, pos)
            ?: return -1
        var msecs = datetime.time
        if (subSecs != null) {
            try {
                var sub = subSecs.toLong()
                while (sub > 1000) {
                    sub /= 10
                }
                msecs += sub
            } catch (e: NumberFormatException) {
                // Ignored
            }
        }
        msecs
    } catch (e: IllegalArgumentException) {
        -1
    }
}

private val sNonZeroTimePattern = Pattern.compile(".*[1-9].*")