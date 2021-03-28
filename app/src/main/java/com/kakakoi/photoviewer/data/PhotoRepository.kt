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
                photo.networkPath
            )
        } ?: run {
            photoDao.insertAll(photo)
        }
    }

    fun findByStateWait(): Photo? {
        return photoDao.findByStateWait()
    }

    fun findById(id: Int): LiveData<Photo> {
        return photoDao.findById(id)
    }
}