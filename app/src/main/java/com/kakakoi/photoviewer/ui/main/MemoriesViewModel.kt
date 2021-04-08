package com.kakakoi.photoviewer.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.Photo
import com.kakakoi.photoviewer.lib.Event

class MemoriesViewModel(application: Application) : AndroidViewModel(application) {

    val photos: LiveData<List<Photo>> = getApplication<PhotoViewerApplication>().photoRepository.allSmile

    val onTransit = MutableLiveData<Event<Photo>>()

    fun onClickItem(item: Photo){
        onTransit.value = Event(item)
        Log.d(MainViewModel.TAG, "onClickItem: ${item.id}")
    }
}