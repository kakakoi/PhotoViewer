package com.kakakoi.photoviewer.data

class PhotoRepository(private val photoDao: PhotoDao) {

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
}