package com.kakakoi.photoviewer.ui.setting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.kakakoi.photoviewer.data.Storage
import com.kakakoi.photoviewer.lib.Event
import com.kakakoi.photoviewer.ui.main.MainViewModel

class MainSettingsViewModel : ViewModel() {

    val onTransit = MutableLiveData<Event<String>>()

    private val storagesRaw = mutableListOf<Storage>()
    private val _storages = MutableLiveData<List<Storage>>(emptyList())
    val storages: LiveData<List<Storage>> = _storages.distinctUntilChanged()


    fun onClickItem(item: Storage){
        onTransit.value = Event("onTransit")
        Log.d(MainViewModel.TAG, "onClickItem: ${item.id}")
    }

    fun onClick(){
        onTransit.value = Event("storageSettings")
        Log.d(MainViewModel.TAG, "onClick: ")
    }

}