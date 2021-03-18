package com.kakakoi.photoviewer.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.kakakoi.photoviewer.R
import com.kakakoi.photoviewer.data.Photo

class MainViewModel : ViewModel() {

    companion object{
        const val TAG = "MainViewModel"
    }
    private val photosRaw = mutableListOf<Photo>()
    private val _photos = MutableLiveData<List<Photo>>(emptyList())
    val photos: LiveData<List<Photo>> = _photos.distinctUntilChanged()

    private var index = 0L
    private var name = "test"
    private var resource = R.mipmap.ic_launcher

    fun addElement(){
        val photo = Photo(index, "$name$index", resource)
        photosRaw.add(photo)
        _photos.value = ArrayList(photosRaw)

        index +=1
        Log.d(TAG, "addElement: $index")
    }
}