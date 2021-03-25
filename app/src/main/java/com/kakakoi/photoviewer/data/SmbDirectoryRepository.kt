package com.kakakoi.photoviewer.data

class SmbDirectoryRepository(private val smbDirectoryDao: SmbDirectoryDao) {

    fun insert(smbDirectory: SmbDirectory) {
        smbDirectoryDao.insertAll(smbDirectory)
    }

    fun findByPath(path: String): SmbDirectory{
        return smbDirectoryDao.findByPath(path)
    }

    fun findWait(): SmbDirectory{
        return smbDirectoryDao.findByStatus("wait")
    }

    fun marge(smbDirectory: SmbDirectory) {
        val old = smbDirectoryDao.findByPath(smbDirectory.path)
        old?.apply {
            smbDirectoryDao.update(
                smbDirectory.path,
                smbDirectory.lastUpdateAt,
                smbDirectory.indexCount,
                smbDirectory.fileCount,
                smbDirectory.status
            )
        } ?: run {
            smbDirectoryDao.insertAll(smbDirectory)
        }
    }
}