package com.kakakoi.photoviewer.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photo")
    fun getAll(): LiveData<List<Photo>>

    @Query("SELECT * FROM photo WHERE cache_path IS NOT NULL")
    fun getAllWithCache(): LiveData<List<Photo>>

    @Query("SELECT * FROM photo WHERE id IN (:photoIds)")
    fun loadAllByIds(photoIds: IntArray): List<Photo>

    @Query("SELECT * FROM photo WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Photo

    @Query("SELECT * FROM photo WHERE network_path = :networkPath")
    fun findByNetworkPath(networkPath: String): Photo?

    @Query("SELECT * FROM photo WHERE cache_path IS NULL OR cache_path IS '' LIMIT 1")
    fun findByStateWait(): Photo?

    @Insert
    fun insertAll(vararg photos: Photo)

    @Query("UPDATE photo SET cache_path = :cachePath, date_time_original = :dateTimeOriginal, resource = :resource WHERE network_path = :networkPath")
    fun update(cachePath: String, dateTimeOriginal: String, resource: Int, networkPath: String)

    @Delete
    fun delete(photo: Photo)
}
