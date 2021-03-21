package com.kakakoi.photoviewer.data

import androidx.lifecycle.MutableLiveData
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class Storage(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "resource") val resource: Int,
    @ColumnInfo(name = "is_checked") val isChecked: MutableLiveData<Boolean> = MutableLiveData(false)
)
