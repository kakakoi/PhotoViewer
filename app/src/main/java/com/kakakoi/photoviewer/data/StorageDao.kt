package com.kakakoi.photoviewer.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StorageDao {
    @Query("SELECT * FROM storage")
    fun getAllAsFlow(): Flow<List<Storage>>

    @Query("SELECT * FROM storage")
    fun getAll(): List<Storage>

    @Query("SELECT * FROM storage WHERE id IN (:storageIds)")
    fun loadAllByIds(storageIds: IntArray): List<Storage>

    @Query("SELECT * FROM storage WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Storage

    @Query("SELECT * FROM storage WHERE address = :address AND dir = :dir")
    fun findByAddressAndDir(address: String, dir: String): Storage

    @Query("UPDATE storage SET user = :user, pass = :pass, is_checked = :isChecked WHERE address = :address AND dir = :dir")
    fun update(address: String, dir: String, user: String, pass: String, isChecked: Boolean)

    @Insert
    fun insertAll(vararg storages: Storage)

    @Delete
    fun delete(storage: Storage)
}