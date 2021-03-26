package com.kakakoi.photoviewer.ui.setting

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.SmbStatus
import com.kakakoi.photoviewer.data.Storage
import com.kakakoi.photoviewer.lib.Event
import com.kakakoi.photoviewer.network.SmbIndex
import com.kakakoi.photoviewer.ui.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainSettingsViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "MainSettingsViewModel"
    }
    val onTransit = MutableLiveData<Event<String>>()

    private val storagesRaw = mutableListOf<Storage>()
    private val _storages = MutableLiveData<List<Storage>>(emptyList())
    val allStorages: LiveData<List<Storage>> = (application as PhotoViewerApplication).storageRepository.allStorage.asLiveData()
    private val smbStatusRepo = (application as PhotoViewerApplication).smbStatusRepository
    val smbStatus: LiveData<SmbStatus> = smbStatusRepo.status
    val smbFileSize: LiveData<String> = smbStatusRepo.smbFileSize

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
            list?.forEach{
                val result = runCatching {
                    val smbIndex = SmbIndex.getInstance(getApplication(), it)
                    smbIndex.deepCreateIndex()
                }.onSuccess {
                    Result.success(it)
                    Log.d(TAG, "createIndex: count ${it.toString()}")
                }.onFailure {
                    Result.failure<Throwable>(it)
                    Log.d(TAG, "createIndex: failure ${it.message}")
                }
            }
        }
    }
}