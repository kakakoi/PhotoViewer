package com.kakakoi.photoviewer.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kakakoi.photoviewer.data.AppDatabase
import com.kakakoi.photoviewer.data.StorageRepository
import com.kakakoi.photoviewer.network.SmbLoader
import com.kakakoi.photoviewer.ui.setting.MainSettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoadWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext,
    params
) {
    companion object {
        const val TAG = "LoadWorker"
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            Log.d(IndexWorker.TAG, "doWork: start")
            try {
                load()
                Log.d(IndexWorker.TAG, "doWork: finish")
                Result.success()
            } catch (throwable: Throwable) {
                Log.e(IndexWorker.TAG, "doWork: ", throwable)
                Result.failure()
            }
        }
    }

    private  fun load(){
        val repo = StorageRepository(AppDatabase.getDatabase(applicationContext).storageDao())
        val list = repo.findAll()
        list?.forEach{
            val result = runCatching {
                val smbLoader = SmbLoader.getInstance(applicationContext, it)
                smbLoader.load()
            }.onSuccess {
                kotlin.Result.success(it)
                Log.d(MainSettingsViewModel.TAG, "load: count ${it.toString()}")
            }.onFailure {
                kotlin.Result.failure<Throwable>(it)
                Log.d(MainSettingsViewModel.TAG, "load: failure ${it.message}")
            }
        }
    }
}