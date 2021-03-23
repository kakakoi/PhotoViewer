package com.kakakoi.photoviewer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StorageDao {
    @Query("SELECT * FROM storage")
    fun getAll(): Flow<List<Storage>>

    @Query("SELECT * FROM storage WHERE id IN (:storageIds)")
    fun loadAllByIds(storageIds: IntArray): List<Storage>

    @Query("SELECT * FROM storage WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Storage

    @Insert
    fun insertAll(vararg storages: Storage)

    @Delete
    fun delete(storage: Storage)
}