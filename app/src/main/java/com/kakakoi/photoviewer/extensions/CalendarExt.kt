package com.kakakoi.photoviewer.extensions

import android.text.format.DateFormat
import java.util.*

/**
 * 現在日時をyyyy/MM/dd HH:mm:ss形式で取得する.<br></br>
 */
fun Calendar.getNowDate(): String {
    return DateFormat.format("yyyy/MM/dd kk:mm:ss", Calendar.getInstance()) as String
}

fun Calendar.getNowTimeInMillis(): Long {
    return Calendar.getInstance().timeInMillis
}