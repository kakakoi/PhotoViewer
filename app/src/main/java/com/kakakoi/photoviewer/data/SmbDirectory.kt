package com.kakakoi.photoviewer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SmbDirectory(
    @PrimaryKey
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "storage_id") val storageId: Int,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "last_update_at") val lastUpdateAt: String,
    @ColumnInfo(name = "file_count") val fileCount: Int,
    @ColumnInfo(name = "index_count") val indexCount: Int,
    @ColumnInfo(name = "status") val status: String,
    )
