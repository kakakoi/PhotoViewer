package com.kakakoi.photoviewer.network

import android.content.Context
import android.util.Log
import com.kakakoi.photoviewer.data.*
import com.kakakoi.photoviewer.extensions.downloadShrinkPhoto

class SmbLoader (
    private val context: Context,
    private val storage: Storage
) {
    companion object {
        private val TAG = "SmbLoader"
        private var instance: SmbLoader? = null
        private var smb:Smb? = null

        private var photoRepository:PhotoRepository? = null
        private var smbStatusRepository: SmbStatusRepository? = null

        fun getInstance(context: Context, storage: Storage) = instance ?: synchronized(this) {
            instance ?: SmbLoader(
                context,
                storage
            ).also {
                instance = it

                val database = AppDatabase.getDatabase(context)
                smbStatusRepository = SmbStatusRepository()
                photoRepository = PhotoRepository(database.photoDao())

                smb = Smb(storage)
            }
        }
    }

    private fun isRunning(): Boolean {
        return photoRepository?.let { true } ?: false
    }

    private fun finish(){
        if(isRunning()) {
            throw IllegalStateException("already running")
        } else {
            photoRepository = null
            SmbLoader.instance = null
        }
    }

    fun load(): Int{
        var counter = 0
        var photo: Photo? = null
        photoRepository?.let { pr ->
            do {
                photo?.let{ p ->
                    smb?.let { s ->
                        Log.d(TAG, "load: connect photoId=${p.id}")
                        val smbFile = s.connect(p.networkPath)
                        smbFile?.let { sf ->
                            p.cachePath = sf.downloadShrinkPhoto(context, s.prefixBasePath)
                            pr.marge(p)
                            counter += 1
                            Log.d(TAG, "load: cachePath ${p.cachePath}")
                            updateStatus(status = SmbState.LOAD.name, path = sf.name, size = sf.length())
                        }
                    }
                }
                photo = pr.findByStateWait()
            } while (photo != null)
        }
        finish()
        return counter
    }

    private fun updateStatus(id: String = "", status: String = "", path: String = "", size: Long = 0L) {
        smbStatusRepository?.update(
            SmbStatus(
                id,
                status,
                path,
                size
            )
        )
    }
}