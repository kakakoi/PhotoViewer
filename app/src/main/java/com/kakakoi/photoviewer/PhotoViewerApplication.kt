package com.kakakoi.photoviewer

import android.app.Application
import com.kakakoi.photoviewer.data.*

class PhotoViewerApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val settingRepository by lazy { SettingRepository(database.settingDao()) }
    val storageRepository by lazy { StorageRepository(database.storageDao()) }
    val smbStatusRepository by lazy { SmbStatusRepository() }
    val smbDirectoryRepository by lazy { SmbDirectoryRepository(database.smbDirectoryDao()) }
    val photoRepository by lazy { PhotoRepository(database.photoDao())}
}