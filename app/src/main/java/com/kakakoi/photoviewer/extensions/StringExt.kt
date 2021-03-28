package com.kakakoi.photoviewer.extensions

/**
 * 拡張子取得
 */
fun String.mimetype(): String{
    val point: Int = this.lastIndexOf(".")
    return if (point != -1) {
        this.substring(point + 1)
    } else {
        ""
    }
}