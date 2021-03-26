package com.kakakoi.photoviewer.data

import com.kakakoi.photoviewer.network.SmbState

class SmbDirectoryRepository(private val smbDirectoryDao: SmbDirectoryDao) {

    fun insert(smbDirectory: SmbDirectory) {
        smbDirectoryDao.insertAll(smbDirectory)
    }

    fun findByPath(path: String): SmbDirectory{
        return smbDirectoryDao.findByPath(path)
    }

    fun findWait(): SmbDirectory? {
        return smbDirectoryDao.findByStatus(SmbState.WAIT.name)
    }

    fun marge(smbDirectory: SmbDirectory) {
        val old = smbDirectoryDao.findByPath(smbDirectory.path)
        old?.apply {
            val oldState = SmbState.valueOf(this.status)
            val newState = SmbState.valueOf(smbDirectory.status)
            if(oldState < newState) {
                smbDirectoryDao.update(
                    smbDirectory.path,
                    smbDirectory.lastUpdateAt,
                    smbDirectory.indexCount,
                    smbDirectory.fileCount,
                    smbDirectory.status
                )
            }
        } ?: run {
            smbDirectoryDao.insertAll(smbDirectory)
        }
    }
}