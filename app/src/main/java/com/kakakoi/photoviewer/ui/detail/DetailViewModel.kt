package com.kakakoi.photoviewer.ui.detail

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import com.kakakoi.photoviewer.PhotoViewerApplication
import com.kakakoi.photoviewer.data.Photo
import com.kakakoi.photoviewer.lib.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DetailViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        const val TAG = "DetailViewModel"
    }
    private val photoRepo = (application as PhotoViewerApplication).photoRepository
    private var _photo: MutableLiveData<Photo> = MutableLiveData<Photo>().also { it.value = emptyData() }
    var photo: LiveData<Photo> = _photo
        private set
    val onTransit = MutableLiveData<Event<String>>()

    fun setPhotoId(id: Int) {
        photo = getApplication<PhotoViewerApplication>().photoRepository.findById(id)
    }

    fun onClickItem(item: Photo){
        onTransit.value = Event("onTransit")
        Log.d(TAG, "onClickItem: ${item.id}")
    }

    fun emptyData(): Photo {
        return Photo(
            0,
            "",
            0,
            0,
            0,
            "",
            "",
            "",
            0.0
        )
    }

    private fun imageFromBitmap(bitmap: Bitmap): InputImage {
        val rotationDegree = 0
        // [START image_from_bitmap]
        return InputImage.fromBitmap(bitmap, rotationDegree)
        // [END image_from_bitmap]
    }

    fun detectFaces() {
        val path = "${getApplication<PhotoViewerApplication>().filesDir}/${photo.value?.cachePath}"
        Log.d(TAG, "detectFaces: bitmap path $path")
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
                Log.d(TAG, "detectFaces: faces size ${faces.size}")

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
                        Log.d(TAG, "detectFaces: smileProb ${smileProb.toString()}")
                        //TODO networkPath > id
                        photo.value?.networkPath.let {
                            if(!it.isNullOrBlank()) {
                                viewModelScope.launch {
                                    withContext(Dispatchers.Default) {
                                        photoRepo.updateSmiling(it, smileProb?.toDouble())
                                        Log.d(TAG, "detectFaces: networkpath $it, smil ${smileProb.toString()}")
                                    }
                                }
                            }
                        }
                        Log.d(TAG, "detectFaces: smileProb ${smileProb.toString()}")
                    }
                    if (face.rightEyeOpenProbability != null) {
                        val rightEyeOpenProb = face.rightEyeOpenProbability
                        Log.d(TAG, "detectFaces: rightEyeOpenProb ${rightEyeOpenProb.toString()}")
                    }

                    // If face tracking was enabled:
                    if (face.trackingId != null) {
                        val id = face.trackingId
                        Log.d(TAG, "detectFaces: face.trackingId $id")
                    }
                }
                // [END get_face_info]
                // [END_EXCLUDE]
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "detectFaces: ${e.message}")
                // Task failed with an exception
                // ...
            }
        // [END run_detector]
    }
}