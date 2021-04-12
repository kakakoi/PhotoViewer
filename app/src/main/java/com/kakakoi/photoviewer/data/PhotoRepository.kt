package com.kakakoi.photoviewer.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotoRepository(private val photoDao: PhotoDao) {

    val allPhotos: LiveData<List<Photo>> = photoDao.getAllWithCache()
    val allSmile: LiveData<List<Photo>> = photoDao.getAllSmile()

    fun marge(photo: Photo) {
        val old = photoDao.findByNetworkPath(photo.networkPath)
        old?.run {

            //marge
            photo.smiling = old.smiling?.let {
                val smile = photo.smiling ?: 0.0
                if(it > smile) it else smile
            }
            photo.dateTimeOriginal = if(photo.dateTimeOriginal < 0L) old.dateTimeOriginal else photo.dateTimeOriginal
            photo.cachePath = if(photo.cachePath.isNullOrBlank()) old.cachePath else photo.cachePath

            photoDao.update(
                photo.cachePath!!,
                photo.dateTimeOriginal,
                photo.resource,
                photo.networkPath,
                photo.smiling
            )
        } ?: run {
            photoDao.insertAll(photo)
        }
    }

    suspend fun updateSmiling(networkPath: String, smiling: Double?) {
        withContext(Dispatchers.IO) {
            photoDao.updateSmiling(networkPath, smiling)
        }
    }

    fun findUnAnalyzed(limit: Int): List<Photo>? {
        return photoDao.getUnAnalyzed(limit)
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