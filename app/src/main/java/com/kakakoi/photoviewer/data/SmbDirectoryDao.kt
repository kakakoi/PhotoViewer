package com.kakakoi.photoviewer.data

import androidx.room.*

@Dao
interface SmbDirectoryDao {
    @Query("SELECT * FROM smbdirectory")
    fun getAll(): List<SmbDirectory>

    @Query("SELECT * FROM smbdirectory WHERE path = :path")
    fun findByPath(path: String): SmbDirectory

    @Query("SELECT * FROM smbdirectory WHERE status = :status ORDER BY LENGTH(path) ASC LIMIT 1")
    fun findByStatus(status: String): SmbDirectory?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg smbDirectorys: SmbDirectory)

    @Query("UPDATE smbdirectory SET last_update_at = :lastUpdateAt, index_count = :indexCount, file_count = :fileCount, status =:status WHERE path = :path")
    fun update(path: String, lastUpdateAt: String, indexCount: Int, fileCount: Int, status: String)

    @Delete
    fun delete(smbDirectory: SmbDirectory)
}