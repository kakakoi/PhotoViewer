package com.kakakoi.photoviewer

import android.app.Application
import com.kakakoi.photoviewer.data.AppDatabase
import com.kakakoi.photoviewer.data.SettingRepository

class PhotoViewerApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val settingRepository by lazy { SettingRepository(database.settingDao()) }
}