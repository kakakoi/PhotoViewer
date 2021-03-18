package com.kakakoi.photoviewer.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class SettingRepository (private val settingDao: SettingDao) {

    val allSetting: Flow<List<Setting>> = settingDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(setting: Setting) {
        settingDao.insertAll(setting)
    }
}