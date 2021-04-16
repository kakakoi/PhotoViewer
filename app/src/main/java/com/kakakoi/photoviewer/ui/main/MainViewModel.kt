package com.kakakoi.photoviewer.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.*
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.Photo
import com.kakakoi.photoviewer.lib.Event
import com.kakakoi.photoviewer.worker.FaceDetectWorker
import com.kakakoi.photoviewer.worker.IndexWorker
import com.kakakoi.photoviewer.worker.LoadWorker
import kotlinx.coroutines.flow.Flow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        const val TAG = "MainViewModel"
        const val TAG_OUTPUT = "OUTPUT"
    }
    val pRepo = getApplication<PhotoViewerApplication>().photoRepository
    val photos: Flow<PagingData<Photo>> = pRepo.photoStream.cachedIn(viewModelScope)

    val countAll = pRepo.countAll().distinctUntilChanged()
    val countWait = pRepo.countWait().distinctUntilChanged()
    val countLoad = pRepo.countLoad().distinctUntilChanged()

    val onTransit = MutableLiveData<Event<Photo>>()

    private val workManager = WorkManager.getInstance(application)
    internal val outputWorkInfos: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)

    fun onClickItem(item: Photo){
        onTransit.value = Event(item)
        Log.d(TAG, "onClickItem: ${item.id}")
    }

    fun enqueueWorks() {
        applyWorker()
    }

    private fun buildFaceDetectWorker():OneTimeWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()

        return OneTimeWorkRequestBuilder<FaceDetectWorker>()
            .setConstraints(constraints)
            .build()
    }

    private fun buildIndexWork():OneTimeWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()

        return OneTimeWorkRequestBuilder<IndexWorker>()
            .setConstraints(constraints)
            .build()
    }

    private fun buildLoadWork():OneTimeWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()

        return OneTimeWorkRequestBuilder<LoadWorker>()
            .setConstraints(constraints)
            .build()
    }

    private fun applyWorker() {
        workManager.beginUniqueWork(
            "crawling",
            ExistingWorkPolicy.KEEP,
            buildIndexWork()
        )
            .then(buildLoadWork())
            .then(buildFaceDetectWorker())
            .enqueue()
    }
}