package com.kakakoi.photoviewer.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.Photo
import com.kakakoi.photoviewer.lib.Event
import com.kakakoi.photoviewer.worker.FaceDetectWorker
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        const val TAG = "MainViewModel"
        const val TAG_OUTPUT = "OUTPUT"
    }
    val photos: LiveData<List<Photo>> = getApplication<PhotoViewerApplication>().photoRepository.allPhotos

    val onTransit = MutableLiveData<Event<Photo>>()

    private val workManager = WorkManager.getInstance(application)
    internal val outputWorkInfos: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)

    fun onClickItem(item: Photo){
        onTransit.value = Event(item)
        Log.d(TAG, "onClickItem: ${item.id}")
    }

    fun applyFaceDetect() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()

        val faceDetectRequest =
            PeriodicWorkRequestBuilder<FaceDetectWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            "faceDetect",
            //ExistingPeriodicWorkPolicy.KEEP,
            ExistingPeriodicWorkPolicy.REPLACE,
            faceDetectRequest
        )
    }
}