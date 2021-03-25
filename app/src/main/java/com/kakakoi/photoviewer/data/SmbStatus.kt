package com.kakakoi.photoviewer.data

data class SmbStatus(
    val id: String,
    val status: String,
    val path: String,
    val size: Long,
    val running: Boolean
)
