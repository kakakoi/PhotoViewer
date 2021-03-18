package com.kakakoi.photoviewer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao {
    @Query("SELECT * FROM setting")
    fun getAll(): Flow<List<Setting>>

    @Query("SELECT * FROM setting WHERE id IN (:settingIds)")
    fun loadAllByIds(settingIds: IntArray): List<Setting>

    @Query("SELECT * FROM setting WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Setting

    @Insert
    fun insertAll(vararg settings: Setting)

    @Delete
    fun delete(setting: Setting)
}