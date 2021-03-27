package com.kakakoi.photoviewer.network

import android.util.Log
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.Photo
import com.kakakoi.photoviewer.data.PhotoRepository
import com.kakakoi.photoviewer.data.SmbStatus
import com.kakakoi.photoviewer.data.Storage
import com.kakakoi.photoviewer.extensions.downloadShrinkPhoto

class SmbLoader (
    private val application: PhotoViewerApplication,
    private val storage: Storage
) {
    companion object {
        private val TAG = "SmbLoader"
        private var instance: SmbLoader? = null
        private var smb:Smb? = null
        private var photoRepository:PhotoRepository? = null

        fun getInstance(application: PhotoViewerApplication, storage: Storage) = instance ?: synchronized(this) {
            instance ?: SmbLoader(
                application,
                storage
            ).also {
                instance = it
                smb = Smb(application, storage)
            }
        }
    }

    private fun isRunning(): Boolean {
        return photoRepository?.let { true } ?: false
    }

    private fun start() {
        photoRepository = application.photoRepository
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
        start()
        var photo: Photo? = null
        photoRepository?.let { pr ->
            do {
                photo?.let{ p ->
                    smb?.let { s ->
                        val smbFile = s.connect(p.networkPath)
                        smbFile?.let { sf ->
                            p.cachePath = sf.downloadShrinkPhoto(application, s.baseUrl)
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
        application.smbStatusRepository.update(
            SmbStatus(
                id,
                status,
                path,
                size
            )
        )
    }
}