package com.kakakoi.photoviewer.data

import androidx.lifecycle.MutableLiveData

data class Photo(
    val id: Long,
    val name: String,
    val resource: Int,
    val isChecked: MutableLiveData<Boolean> = MutableLiveData(false)
)