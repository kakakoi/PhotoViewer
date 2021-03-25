package com.kakakoi.photoviewer.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged

class SmbStatusRepository {
    private val _status = MutableLiveData<SmbStatus>()
    val status: LiveData<SmbStatus> = _status.distinctUntilChanged()

    fun update(smbStatus: SmbStatus){
        _status.postValue(smbStatus)
    }
}