package com.kakakoi.photoviewer.data

import androidx.lifecycle.MutableLiveData
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "resource") val resource: Int,
    @ColumnInfo(name = "is_checked") val isChecked: Boolean = false
)