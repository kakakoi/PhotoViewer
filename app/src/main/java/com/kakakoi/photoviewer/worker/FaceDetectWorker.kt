package com.kakakoi.photoviewer.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kakakoi.photoviewer.data.AppDatabase
import com.kakakoi.photoviewer.data.PhotoRepository
import com.kakakoi.photoviewer.ml.FaceDetect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FaceDetectWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext,
    params
) {
    companion object {
        const val LIMIT_WORK = 100
        const val TAG = "FaceDetectWorker"
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "doWork: start")
            try {
                val repo = PhotoRepository(AppDatabase.getDatabase(applicationContext).photoDao())

                FaceDetect(repo, applicationContext.filesDir.path).detectFaces(LIMIT_WORK)
                Log.d(TAG, "doWork: finish")
                Result.success()
            } catch (throwable: Throwable) {
                Log.e(TAG, "doWork: ", throwable)
                Result.failure()
            }
        }
    }
}