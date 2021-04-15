package com.kakakoi.photoviewer.ml

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import com.kakakoi.photoviewer.data.Photo
import com.kakakoi.photoviewer.data.PhotoRepository
import com.kakakoi.photoviewer.ui.detail.DetailViewModel
import kotlinx.coroutines.*

class FaceDetect(
    private val photoRepo: PhotoRepository,
    private val filesDir: String
) {

    companion object {
        const val LIMIT_ANALYZED = 100
        const val TAG = "FaceDetect"
    }

    private fun imageFromBitmap(bitmap: Bitmap): InputImage {
        val rotationDegree = 0
        // [START image_from_bitmap]
        return InputImage.fromBitmap(bitmap, rotationDegree)
        // [END image_from_bitmap]
    }

    suspend fun detectFaces(limit: Int = LIMIT_ANALYZED){
        val photos = photoRepo.findUnAnalyzed(limit)
        photos?.also {
            Log.d(TAG, "detectFaces: UnAnalyzed photos size${it.size}")
            it.forEach {
                detectFaces(it)
            }
        }
    }

    suspend fun detectFaces(photo: Photo) {
        val path = "${filesDir}/${photo.cachePath}"
        Log.d(DetailViewModel.TAG, "detectFaces: bitmap path $path")
        val bitmap = BitmapFactory.decodeFile(path)

        val image = imageFromBitmap(bitmap)
        // [START set_detector_options]
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()
        // [END set_detector_options]

        // [START get_detector]
        val detector = FaceDetection.getClient(options)
        // Or, to use the default option:
        // val detector = FaceDetection.getClient();
        // [END get_detector]

        // [START run_detector]
        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                Log.d(DetailViewModel.TAG, "detectFaces: faces size ${faces.size}")

                // Task completed successfully
                // [START_EXCLUDE]
                // [START get_face_info]
                for (face in faces) {
                    val bounds = face.boundingBox
                    val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                    val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                    // nose available):
                    val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                    leftEar?.let {
                        val leftEarPos = leftEar.position
                    }

                    // If classification was enabled:
                    if (face.smilingProbability != null) {
                        val smileProb = face.smilingProbability
                        Log.d(DetailViewModel.TAG, "detectFaces: smileProb ${smileProb.toString()}")
                        if(!photo.networkPath.isNullOrBlank()) {
                            runBlocking {
                                photoRepo.updateSmiling(photo.networkPath, smileProb?.toDouble())
                            }
                            Log.d(DetailViewModel.TAG, "detectFaces: networkpath $photo.networkPath, smil ${smileProb.toString()}")
                        }
                        Log.d(DetailViewModel.TAG, "detectFaces: smileProb ${smileProb.toString()}")
                    }
                    if (face.rightEyeOpenProbability != null) {
                        val rightEyeOpenProb = face.rightEyeOpenProbability
                        Log.d(DetailViewModel.TAG, "detectFaces: rightEyeOpenProb ${rightEyeOpenProb.toString()}")
                    }

                    // If face tracking was enabled:
                    if (face.trackingId != null) {
                        val id = face.trackingId
                        Log.d(DetailViewModel.TAG, "detectFaces: face.trackingId $id")
                    }
                }
                // [END get_face_info]
                // [END_EXCLUDE]
            }
            .addOnFailureListener { e ->
                Log.d(DetailViewModel.TAG, "detectFaces: ${e.message}")
                // Task failed with an exception
                // ...
            }
        // [END run_detector]
    }
}