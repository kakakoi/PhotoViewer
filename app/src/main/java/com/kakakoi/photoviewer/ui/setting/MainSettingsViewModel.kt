package com.kakakoi.photoviewer.ui.setting

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.SmbStatus
import com.kakakoi.photoviewer.data.Storage
import com.kakakoi.photoviewer.lib.Event
import com.kakakoi.photoviewer.ui.main.MainViewModel

class MainSettingsViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "MainSettingsViewModel"
    }
    val onTransit = MutableLiveData<Event<String>>()

    private val storagesRaw = mutableListOf<Storage>()
    private val _storages = MutableLiveData<List<Storage>>(emptyList())
    private val app = (application as PhotoViewerApplication)
    val allStorages: LiveData<List<Storage>> = app.storageRepository.allStorage.asLiveData()
    private val smbStatusRepo = app.smbStatusRepository
    val smbStatus: LiveData<SmbStatus> = smbStatusRepo.status
    val smbFileSize: LiveData<Long> = smbStatusRepo.smbFileSize

    private val photoRepo = app.photoRepository
    val countAllPhotos = photoRepo.countAll()
    val countWaitPhotos = photoRepo.countWait()
    val countLoadPhotos = photoRepo.countLoad()

    fun onClickItem(item: Storage){
        onTransit.value = Event("onTransit")
        Log.d(MainViewModel.TAG, "onClickItem: ${item.id}")
    }

    fun onClick(){
        onTransit.value = Event("storageSettings")
        Log.d(MainViewModel.TAG, "onClick: ")
    }
}