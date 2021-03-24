package com.kakakoi.photoviewer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Storage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")val id: Int = 0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "resource") val resource: Int = 0,
    @ColumnInfo(name = "address") val address: String = "",
    @ColumnInfo(name = "user") val user: String = "",
    @ColumnInfo(name = "pass") val pass: String = "",
    @ColumnInfo(name = "dir") val dir: String = "",
    @ColumnInfo(name = "is_checked") var isChecked: Boolean = false
)
