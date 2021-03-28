package com.kakakoi.photoviewer.network

enum class SmbState {
    WAIT{ override fun next(): SmbState = INDEX },
    INDEX{ override fun next(): SmbState =LOAD },
    LOAD{ override fun next(): SmbState = LOAD };

    abstract fun next(): SmbState
}