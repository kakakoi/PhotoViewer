package com.kakakoi.photoviewer.data

import android.text.format.Formatter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged

class SmbStatusRepository() {

    private val _status = MutableLiveData<SmbStatus>(emptyData())
    //private val _smbFileSize = MutableLiveData<String>()
    private val _smbFileSize = MutableLiveData<Long>()
    val status: LiveData<SmbStatus> = _status.distinctUntilChanged()
    //val smbFileSize: LiveData<String> = _smbFileSize.distinctUntilChanged()
    val smbFileSize: LiveData<Long> = _smbFileSize.distinctUntilChanged()

    fun update(smbStatus: SmbStatus){
        _status.postValue(smbStatus)
        //_smbFileSize.postValue(Formatter.formatShortFileSize(context, smbStatus.size))
        _smbFileSize.postValue(smbStatus.size)
    }

    private fun emptyData():SmbStatus {
        return SmbStatus(
            "",
            "",
            "",
            0
        )
    }
}