package com.kakakoi.photoviewer

import android.app.Application
import com.kakakoi.photoviewer.data.AppDatabase
import com.kakakoi.photoviewer.data.SettingRepository
import com.kakakoi.photoviewer.data.StorageRepository

class PhotoViewerApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val settingRepository by lazy { SettingRepository(database.settingDao()) }
    val storageRepository by lazy { StorageRepository(database.storageDao()) }

}