package com.kakakoi.photoviewer.data

import android.app.Application
import androidx.preference.PreferenceManager
import com.kakakoi.photoviewer.ui.setting.StorageSettingsFragment
import kotlinx.coroutines.flow.Flow

class StorageRepository(private val storageDao: StorageDao) {

    val allStorage: Flow<List<Storage>> = storageDao.getAllAsFlow()

    fun getSharedPreferenceStorage(application: Application): Storage{
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
        return Storage(
            address = sharedPreferences.getString(StorageSettingsFragment.KEY_SMB_IP, "")!!,
            user = sharedPreferences.getString(StorageSettingsFragment.KEY_SMB_USER, "")!!,
            pass = sharedPreferences.getString(StorageSettingsFragment.KEY_SMB_PASS, "")!!,
            dir = sharedPreferences.getString(StorageSettingsFragment.KEY_SMB_DIR, "")!!
        )
    }

    fun insert(storage: Storage) {
        storageDao.insertAll(storage)
    }

    fun marge(storage: Storage) {
        val old = storageDao.findByAddressAndDir(storage.address, storage.dir)
        old?.apply {
            storageDao.update(storage.address, storage.dir, storage.user, storage.pass, storage.isChecked)
        } ?: run {
            storageDao.insertAll(storage)
        }
    }

    fun findAll(): List<Storage> {
        return storageDao.getAll()
    }
}