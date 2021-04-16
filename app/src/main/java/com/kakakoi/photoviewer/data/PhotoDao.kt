package com.kakakoi.photoviewer.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photo ORDER BY date_time_original DESC")
    fun getAll(): LiveData<List<Photo>>

    @Query("SELECT * FROM photo WHERE cache_path IS NOT NULL AND cache_path != '' ORDER BY date_time_original DESC")
    fun getAllWithCache(): LiveData<List<Photo>>

    @Query("SELECT * FROM photo WHERE cache_path IS NOT NULL AND cache_path != '' AND smiling > 0.5 ORDER BY date_time_original DESC")
    fun getAllSmile(): LiveData<List<Photo>>

    @Query("SELECT * FROM photo WHERE cache_path IS NOT NULL AND cache_path != '' AND smiling > 0.5 ORDER BY date_time_original DESC LIMIT :limit")
    fun getSmile(limit: Int): LiveData<List<Photo>>

    @Query("SELECT * FROM photo WHERE cache_path IS NOT NULL AND cache_path != '' AND (smiling IS NULL OR smiling = 0) LIMIT :limit")
    fun getUnAnalyzed(limit: Int): List<Photo>?

    @Query("SELECT * FROM photo WHERE id IN (:photoIds)")
    fun loadAllByIds(photoIds: IntArray): List<Photo>

    @Query("SELECT * FROM photo WHERE id = :id")
    fun findById(id: Int): LiveData<Photo>

    @Query("SELECT * FROM photo WHERE network_path = :networkPath")
    fun findByNetworkPath(networkPath: String): Photo?

    @Query("SELECT * FROM photo WHERE network_path IS NOT NULL AND network_path IS NOT '' AND (cache_path IS NULL OR cache_path IS '') LIMIT 1")
    fun findByStateWait(): Photo?

    @Query("SELECT * FROM photo WHERE id != :id AND network_path IS NOT NULL AND network_path IS NOT '' AND (cache_path IS NULL OR cache_path IS '') LIMIT 1")
    fun findNextByStateWait(id: Int): Photo?

    @Query("SELECT COUNT(id) FROM photo")
    fun countAll(): LiveData<Int>

    @Query("SELECT COUNT(id) FROM photo WHERE cache_path IS NULL OR cache_path IS ''")
    fun countWait(): LiveData<Int>

    @Query("SELECT COUNT(id) FROM photo WHERE cache_path IS NOT NULL AND cache_path != ''")
    fun countLoad(): LiveData<Int>

    @Insert
    suspend fun insertAll(vararg photos: Photo)

    @Query("UPDATE photo SET cache_path = :cachePath, date_time_original = :dateTimeOriginal, resource = :resource, smiling = :smiling WHERE network_path = :networkPath")
    fun update(cachePath: String, dateTimeOriginal: Long, resource: Int, networkPath: String, smiling: Double?)

    @Query("UPDATE photo SET smiling = :smiling WHERE network_path =:networkPath")
    fun updateSmiling(networkPath: String, smiling:Double?)

    @Delete
    fun delete(photo: Photo)

    @Query("SELECT * FROM photo WHERE cache_path IS NOT NULL AND cache_path != '' ORDER BY date_time_original DESC LIMIT :limit OFFSET :offset")
    fun pagingPhotos(offset: Int, limit: Int): List<Photo>
}
