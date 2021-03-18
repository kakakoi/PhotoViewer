package com.kakakoi.photoviewer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photo")
    fun getAll(): List<Photo>

    @Query("SELECT * FROM photo WHERE id IN (:photoIds)")
    fun loadAllByIds(photoIds: IntArray): List<Photo>

    @Query("SELECT * FROM photo WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Photo

    @Insert
    fun insertAll(vararg photos: Photo)

    @Delete
    fun delete(photo: Photo)
}
