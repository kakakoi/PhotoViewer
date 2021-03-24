package com.kakakoi.photoviewer.ui.setting

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.Storage
import com.kakakoi.photoviewer.lib.Event
import com.kakakoi.photoviewer.ui.main.MainViewModel

class MainSettingsViewModel(application: Application) : AndroidViewModel(application) {

    val onTransit = MutableLiveData<Event<String>>()

    private val storagesRaw = mutableListOf<Storage>()
    private val _storages = MutableLiveData<List<Storage>>(emptyList())
    val allStorages: LiveData<List<Storage>> = (application as PhotoViewerApplication).storageRepository.allStorage.asLiveData()


    fun onClickItem(item: Storage){
        onTransit.value = Event("onTransit")
        Log.d(MainViewModel.TAG, "onClickItem: ${item.id}")
    }

    fun onClick(){
        onTransit.value = Event("storageSettings")
        Log.d(MainViewModel.TAG, "onClick: ")
    }
}