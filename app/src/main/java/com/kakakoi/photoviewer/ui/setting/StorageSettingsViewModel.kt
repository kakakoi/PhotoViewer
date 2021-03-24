package com.kakakoi.photoviewer.ui.setting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.Storage
import com.kakakoi.photoviewer.lib.Event
import com.kakakoi.photoviewer.network.Smb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StorageSettingsViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        const val TAG = "StorageSettingsViewModel"
    }

    val TRANSIT_SUCCESS = "onTransit"
    val TRANSIT_FAILURE = "onFailure"
    val onTransit = MutableLiveData<Event<String>>()
    val onProgress = MutableLiveData<Boolean>(false)

    fun addStorage(){
        viewModelScope.launch(Dispatchers.IO) {
            onProgress.postValue(true)
            var storage = getApplication<PhotoViewerApplication>().storageRepository
                .getSharedPreferenceStorage(getApplication())
            if(storage.address.isNullOrEmpty()){
                onTransit.postValue(Event(TRANSIT_FAILURE))
                onProgress.postValue(false)
                return@launch
            }
            val smb = Smb(storage)
            val smbFile = smb.connect()

            val result = runCatching {
                smbFile?.list()
            }.onSuccess {
                Result.success(it)
            }.onFailure {
                Result.failure<Throwable>(it)
            }
            val list = result.getOrDefault(emptyArray())!!

            if(result.isSuccess && list.isNotEmpty()) {
                storage.isChecked = true
                insertStorage(storage)
                onTransit.postValue(Event(TRANSIT_SUCCESS))
            } else {
                onTransit.postValue(Event(TRANSIT_FAILURE))
            }
            smbFile?.close()
            onProgress.postValue(false)
        }
    }

    fun insertStorage(storage: Storage){
        getApplication<PhotoViewerApplication>().storageRepository.marge(storage)
    }
}