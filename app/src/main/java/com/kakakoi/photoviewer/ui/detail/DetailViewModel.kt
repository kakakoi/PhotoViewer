package com.kakakoi.photoviewer.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.Photo
import com.kakakoi.photoviewer.lib.Event

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        const val TAG = "DetailViewModel"
    }
    private var _photo: MutableLiveData<Photo> = MutableLiveData<Photo>().also { it.value = emptyData() }
    var photo: LiveData<Photo> = _photo
        private set
    val onTransit = MutableLiveData<Event<String>>()

    fun setPhotoId(id: Int) {
        photo = getApplication<PhotoViewerApplication>().photoRepository.findById(id)
    }

    fun onClickItem(item: Photo){
        onTransit.value = Event("onTransit")
        Log.d(TAG, "onClickItem: ${item.id}")
    }

    fun emptyData(): Photo {
        return Photo(
            0,
            "",
            "",
            "",
            0,
            "",
            "",
            ""
        )
    }
}