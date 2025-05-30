package com.estholon.running.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.estholon.running.domain.exception.CameraException
import com.estholon.running.domain.model.CameraLensFacing
import com.estholon.running.domain.model.CameraModel
import com.estholon.running.domain.repository.CameraRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraRepositoryImpl @Inject constructor(
    private val context: Context
) : CameraRepository {

    private val _cameraState = MutableStateFlow(CameraModel())
    override val cameraState: StateFlow<CameraModel> = _cameraState.asStateFlow()

    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var camera: Camera? = null
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var recording: Recording? = null

    override suspend fun initializeCamera(
        surfaceProvider: Any,
        lifecycleOwner: Any
    ): Result<Unit> {
        return try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProvider = cameraProviderFuture.get()

            setupCamera(
                surfaceProvider as androidx.camera.core.Preview.SurfaceProvider,
                lifecycleOwner as LifecycleOwner
            )

            _cameraState.value = _cameraState.value.copy(
                isInitialized = true,
                error = null
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CameraRepository", "Error initializing camera", e)
            val error = "Error initializing camera: ${e.message}"
            _cameraState.value = _cameraState.value.copy(error = error)
            Result.failure(CameraException.InitializationFailed(e.message ?: "Unknown error"))
        }
    }

    private fun setupCamera(
        surfaceProvider: androidx.camera.core.Preview.SurfaceProvider,
        lifecycleOwner: LifecycleOwner
    ){
        val lensFacing = when (_cameraState.value.lensFacing){
            CameraLensFacing.FRONT -> CameraSelector.LENS_FACING_FRONT
            CameraLensFacing.BACK -> CameraSelector.LENS_FACING_BACK
        }

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        // Preview
        preview = Preview.Builder().build().also {
            it.setSurfaceProvider(surfaceProvider)
        }

        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        // VideoCapture
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        videoCapture = VideoCapture.withOutput(recorder)

        try {
            cameraProvider?.unbindAll()
            camera = cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                videoCapture
            )

            updateFlashMode()
        } catch (e: Exception) {
            Log.e("CameraRepository", "Use case binding failed", e)
            _cameraState.value = _cameraState.value.copy(error = "Camera binding failed: ${e.message}")
            throw e
        }

    }

    private fun updateFlashMode() {
        camera?.cameraControl?.enableTorch(_cameraState.value.isFlashEnabled)
    }

    override suspend fun capturePhoto(): Result<String> {

        val imageCapture = imageCapture ?: return Result.failure(
            CameraException.PhotoCaptureFailed("Camera not initialized")
        )

        return try {
            val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
                .format(System.currentTimeMillis())

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Running-Image")
                }
            }

            val outputOptions = ImageCapture.OutputFileOptions.Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()

            // Suspend function wrapper for callback
            val capturedUri: String = kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exception: ImageCaptureException) {
                            Log.e("CameraRepository", "Photo capture failed", exception)
                            _cameraState.value = _cameraState.value.copy(error = "Photo capture failed")
                            continuation.resumeWithException(CameraException.PhotoCaptureFailed(exception.message ?: "Photo capture failed"))
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val uri = output.savedUri?.toString()
                            if (uri != null) {
                                Log.d("CameraRepository", "Photo capture succeeded: $uri")
                                _cameraState.value = _cameraState.value.copy(
                                    lastCapturedPhotoUri = uri,
                                    error = null
                                )
                                continuation.resume(uri)
                            } else {
                                Log.e("CameraRepository", "Photo capture succeeded but URI is null")
                                _cameraState.value = _cameraState.value.copy(error = "Photo capture succeeded but URI is null")
                                continuation.resumeWithException(CameraException.PhotoCaptureFailed("Photo URI is null"))
                            }
                        }
                    }
                )
            }
            Result.success(capturedUri)

        } catch (e: Exception) {
            Log.e("CameraRepository", "Error during photo capture or URI processing", e)
            Result.failure(CameraException.PhotoCaptureFailed(e.message ?: "Unknown error"))
        }
    }

    override suspend fun startVideoRecording(): Result<Unit> {
        val videoCapture = this.videoCapture ?: return Result.failure(
            CameraException.VideoRecordingFailed("Camera not initialized")
        )

        return try {
            val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
                .format(System.currentTimeMillis())

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
                }
            }

            val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(
                context.contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            ).setContentValues(contentValues).build()

            recording = videoCapture.output
                .prepareRecording(context, mediaStoreOutputOptions)
                .apply {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO)
                        == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        withAudioEnabled()
                    }
                }
                .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            _cameraState.value = _cameraState.value.copy(isRecording = true)
                        }
                        is VideoRecordEvent.Finalize -> {
                            handleRecordingFinalized(recordEvent)
                        }
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(CameraException.VideoRecordingFailed(e.message ?: "Unknown error"))
        }
    }

    private fun handleRecordingFinalized(recordEvent: VideoRecordEvent.Finalize) {
        if (!recordEvent.hasError()) {
            val uri = recordEvent.outputResults.outputUri.toString()
            Log.d("CameraRepository", "Video capture succeeded: $uri")
            _cameraState.value = _cameraState.value.copy(
                isRecording = false,
                isPaused = false,
                lastRecordedVideoUri = uri,
                recordingDuration = 0L
            )
        } else {
            recording?.close()
            recording = null
            Log.e("CameraRepository", "Video capture ended with error: ${recordEvent.error}")
            _cameraState.value = _cameraState.value.copy(
                isRecording = false,
                isPaused = false,
                error = "Video recording failed"
            )
        }
    }

    override suspend fun stopVideoRecording(): Result<String> {
        return try {
            val currentRecording = recording
            if (currentRecording != null) {
                currentRecording.stop()
                recording = null
                // Note: The actual URI will be provided in the VideoRecordEvent.Finalize callback
                Result.success("Recording stopped") // Placeholder - real URI comes from callback
            } else {
                Result.failure(CameraException.VideoRecordingFailed("No active recording"))
            }
        } catch (e: Exception) {
            Result.failure(CameraException.VideoRecordingFailed(e.message ?: "Unknown error"))
        }
    }

    override suspend fun pauseVideoRecording(): Result<Unit> {
        return try {
            recording?.pause()
            _cameraState.value = _cameraState.value.copy(isPaused = true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(CameraException.VideoRecordingFailed("Failed to pause recording"))
        }
    }

    override suspend fun resumeVideoRecording(): Result<Unit> {
        return try {
            recording?.resume()
            _cameraState.value = _cameraState.value.copy(isPaused = false)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(CameraException.VideoRecordingFailed("Failed to resume recording"))
        }
    }

    override suspend fun switchCamera(
        surfaceProvider: Any,
        lifecycleOwner: Any
    ): Result<Unit> {
        return try {
            val newLensFacing = when (_cameraState.value.lensFacing) {
                CameraLensFacing.BACK -> CameraLensFacing.FRONT
                CameraLensFacing.FRONT -> CameraLensFacing.BACK
            }

            _cameraState.value = _cameraState.value.copy(lensFacing = newLensFacing)

            setupCamera(
                surfaceProvider as androidx.camera.core.Preview.SurfaceProvider,
                lifecycleOwner as LifecycleOwner
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(CameraException.CameraSwitchFailed(e.message ?: "Unknown error"))
        }
    }

    override suspend fun toggleFlash(): Result<Unit> {
        return try {
            val newFlashState = !_cameraState.value.isFlashEnabled
            _cameraState.value = _cameraState.value.copy(isFlashEnabled = newFlashState)
            updateFlashMode()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(CameraException.FlashToggleFailed(e.message ?: "Unknown error"))
        }
    }

    override fun clearError() {
        _cameraState.value = _cameraState.value.copy(error = null)
    }

    override fun cleanup() {
        cameraExecutor.shutdown()
        recording?.close()
        recording = null
    }
}