package com.kakakoi.photoviewer.extensions

fun String.mimetype(): String{
    val point: Int = this.lastIndexOf(".")
    return if (point != -1) {
        this.substring(point + 1)
    } else {
        ""
    }
}