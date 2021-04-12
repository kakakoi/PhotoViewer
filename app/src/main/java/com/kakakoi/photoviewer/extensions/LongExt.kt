package com.kakakoi.photoviewer.extensions

import android.text.format.DateFormat
import java.util.*


fun Long.getYear(): String {
    return DateFormat.format("yyyy", this) as String
}

fun Long.getMonth(): String {
    return DateFormat.format("MM", this) as String
}

fun Long.getYearMonth(): Int {
    return (DateFormat.format("yyyyMM", this) as String).toIntOrNull() ?: 0
}