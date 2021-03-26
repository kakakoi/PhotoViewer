package com.kakakoi.photoviewer.extensions

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import jcifs.smb.SmbFile

val REQUIRED_SIZE = 300
val TAG = "extensions.SmbFileExt"

fun SmbFile.downloadShrinkPhoto(
    application: Application,
    basePath: String
): String{
    val startTime = System.currentTimeMillis()

    val bitmapCompressInt = 100
    val inputSize = this.length()
    val inputPath = this.path
    val inputDir = inputPath.replace(basePath, "")
    val outName = inputDir.replace("/", "_")

    val fileOut = application.openFileOutput(outName, Context.MODE_PRIVATE)
    val bitmap = this.decodeSampledBitmap(REQUIRED_SIZE, REQUIRED_SIZE)
    val result = bitmap?.compress(Bitmap.CompressFormat.JPEG, bitmapCompressInt, fileOut)
    Log.d(TAG, "downloadShrinkPhoto: [${this.path}] > [$outName] time:${(System.currentTimeMillis() - startTime)}")

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

        inputStream.close()
        inputStream = this.inputStream
        //初めのdecodeStreamで
        val bitmap = BitmapFactory.decodeStream(inputStream, null, it)
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