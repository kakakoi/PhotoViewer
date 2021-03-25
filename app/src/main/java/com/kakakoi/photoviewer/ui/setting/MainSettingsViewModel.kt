package com.kakakoi.photoviewer.ui.setting

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.SmbStatus
import com.kakakoi.photoviewer.data.Storage
import com.kakakoi.photoviewer.lib.Event
import com.kakakoi.photoviewer.network.Smb
import com.kakakoi.photoviewer.ui.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainSettingsViewModel(application: Application) : AndroidViewModel(application) {

    val onTransit = MutableLiveData<Event<String>>()

    private val storagesRaw = mutableListOf<Storage>()
    private val _storages = MutableLiveData<List<Storage>>(emptyList())
    val allStorages: LiveData<List<Storage>> = (application as PhotoViewerApplication).storageRepository.allStorage.asLiveData()
    val smbStatus: LiveData<SmbStatus> = (application as PhotoViewerApplication).smbStatusRepository.status

    fun onClickItem(item: Storage){
        onTransit.value = Event("onTransit")
        Log.d(MainViewModel.TAG, "onClickItem: ${item.id}")
    }

    fun onClick(){
        onTransit.value = Event("storageSettings")
        Log.d(MainViewModel.TAG, "onClick: ")
    }

    fun createIndex(){
        viewModelScope.launch(Dispatchers.IO) {
            val list = allStorages.value
            list?.size?.also {
                if(it > 0) {
                    val smb = Smb(getApplication(), list.get(0))
                    smb.connect()?.also {
                        smb.deepCreateIndex(it)
                    }
                }
            }
        }
    }
}