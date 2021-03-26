package com.kakakoi.photoviewer.data

import android.app.Application
import android.text.format.Formatter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged

class SmbStatusRepository(private val application: Application) {
    private val _status = MutableLiveData<SmbStatus>()
    private val _smbFileSize = MutableLiveData<String>()
    val status: LiveData<SmbStatus> = _status.distinctUntilChanged()
    val smbFileSize: LiveData<String> = _smbFileSize.distinctUntilChanged()

    fun update(smbStatus: SmbStatus){
        _status.postValue(smbStatus)
        _smbFileSize.postValue(Formatter.formatShortFileSize(application, smbStatus.size))
    }
}