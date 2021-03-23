package com.kakakoi.photoviewer.ui.setting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.Storage
import com.kakakoi.photoviewer.network.Smb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StorageSettingsViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        const val TAG = "StorageSettingsViewModel"
    }
    fun addStorage(){
        viewModelScope.launch(Dispatchers.IO) {
            var storage = getApplication<PhotoViewerApplication>().storageRepository
                .getSharedPreferenceStorage(getApplication())
            val smb = Smb(storage)
            val smbFile = smb.connect()
            storage.isChecked = smbFile?.canRead() ?: false
            insertStorage(storage)
        }
    }

    fun insertStorage(storage: Storage){
        getApplication<PhotoViewerApplication>().storageRepository.insert(storage)
    }
}