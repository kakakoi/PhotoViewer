package com.kakakoi.photoviewer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Int,//TODO primary key -> network_path. id(select max(id) from photo)
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "date_time_original") val dateTimeOriginal: String,
    @ColumnInfo(name = "resource") val resource: Int,
    @ColumnInfo(name = "cache_path") val cachePath: String,
    @ColumnInfo(name = "network_path") val networkPath: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "is_checked") val isChecked: Boolean = false
)