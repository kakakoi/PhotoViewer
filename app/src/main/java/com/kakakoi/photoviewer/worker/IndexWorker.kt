package com.kakakoi.photoviewer.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kakakoi.photoviewer.data.AppDatabase
import com.kakakoi.photoviewer.data.StorageRepository
import com.kakakoi.photoviewer.network.SmbIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IndexWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext,
    params
) {
    companion object {
        const val TAG = "IndexWorker"
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "doWork: start")
            try {
                createIndex()
                Log.d(TAG, "doWork: finish")
                Result.success()
            } catch (throwable: Throwable) {
                Log.e(TAG, "doWork: ", throwable)
                Result.failure()
            }
        }
    }

    private suspend fun createIndex(){
        val repo = StorageRepository(AppDatabase.getDatabase(applicationContext).storageDao())
        val list = repo.findAll()
        list?.forEach{
            Log.d(TAG, "createIndex: StorageId=${it.id} start..${it.address}/${it.dir}")
            val result = runCatching {
                val smbIndex = SmbIndex.getInstance(applicationContext, it)
                smbIndex.deepCreateIndex()
            }.onSuccess {
                kotlin.Result.success(it)
                Log.d(TAG, "createIndex: count ${it.toString()}")
            }.onFailure {
                kotlin.Result.failure<Throwable>(it)
                Log.d(TAG, "createIndex: failure ${it.message}")
            }
        }
    }
}