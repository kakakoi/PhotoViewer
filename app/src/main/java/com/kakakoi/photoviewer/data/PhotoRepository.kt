package com.kakakoi.photoviewer.data

import androidx.lifecycle.LiveData

class PhotoRepository(private val photoDao: PhotoDao) {

    val allPhotos: LiveData<List<Photo>> = photoDao.getAllWithCache()

    fun marge(photo: Photo) {
        val old = photoDao.findByNetworkPath(photo.networkPath)
        old?.run {
            photoDao.update(
                photo.cachePath ?: "",
                photo.dateTimeOriginal,
                photo.resource,
                photo.networkPath,
                photo.smiling
            )
        } ?: run {
            photoDao.insertAll(photo)
        }
    }

    fun updateSmiling(networkPath: String, smiling: Double?) {
        photoDao.updateSmiling(networkPath, smiling)
    }

    fun findByStateWait(): Photo? {
        return photoDao.findByStateWait()
    }

    fun findById(id: Int): LiveData<Photo> {
        return photoDao.findById(id)
    }

    fun countAll(): LiveData<Int> {
        return photoDao.countAll()
    }

    fun countWait(): LiveData<Int> {
        return photoDao.countWait()
    }

    fun countLoad(): LiveData<Int> {
        return photoDao.countLoad()
    }
}