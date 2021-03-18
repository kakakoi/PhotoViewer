package com.kakakoi.photoviewer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * address,dns,smb_user,smb_pass,root_dir
 */
@Entity
data class Setting(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "value") val value: String,
)
