package com.estholon.running.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
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
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import com.estholon.running.data.di.ApplicationScope
import com.estholon.running.data.di.DefaultDispatcher
import com.estholon.running.domain.exception.CameraException
import com.estholon.running.domain.model.CameraLensFacing
import com.estholon.running.domain.model.CameraModel
import com.estholon.running.domain.repository.CameraRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : CameraRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + defaultDispatcher)

    private val _cameraState = MutableStateFlow(CameraModel())
    override val cameraState: StateFlow<CameraModel> = _cameraState.asStateFlow()

    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var camera: Camera? = null
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var recording: Recording? = null

    init {
        Log.d("CameraRepository", "NEW INSTANCE CREATED: ${this.hashCode()}")
    }

    override suspend fun initializeCamera(
        surfaceProvider: Any,
        lifecycleOwner: Any
    ) : Result<Unit> {
        Log.d("CameraRepository","InitializeCamera called on instance: ${this.hashCode()}")
        if(_cameraState.value.isInitialized){
            Log.d("CameraRepository","Camera already initialized, skipping...")
            return Result.success(Unit)
        }

        return try {
            Log.d("CameraRepository", "Starting camera initialization on instance: ${this.hashCode()}")
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            Log.d("CameraRepository","Getting camera provider...")
            cameraProvider = cameraProviderFuture.get()
            Log.d("CameraRepository","Camera provider obtained sucessfully")
            Log.d("CameraRepository","Setting up camera...")
            withContext(Dispatchers.Main) {
                setupCamera(
                    surfaceProvider as androidx.camera.core.Preview.SurfaceProvider,
                    lifecycleOwner as LifecycleOwner
                )
            }
            Log.d("CameraRepository","Camera setup completed")
            Log.d("CameraRepository", "Updating camera state to initialized on instance: ${this.hashCode()}")

            val currentState = _cameraState.value
            Log.d("CameraRepository", "Current state before update: isInitialized=${currentState.isInitialized}")

            val newState = currentState.copy(
                isInitialized = true,
                error = null
            )

            _cameraState.value = newState
            Log.d("CameraRepository", "State updated directly: isInitialized=${_cameraState.value.isInitialized} on instance: ${this.hashCode()}")

            delay(100)
            Log.d("CameraRepository", "Final verification: isInitialized=${_cameraState.value.isInitialized} on instance: ${this.hashCode()}")

            Log.d("CameraRepository", "Camera initialization completed successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CameraRepository", "Error initializing camera on instance: ${this.hashCode()}", e)
            val error = "Error initializing camera: ${e.message}"
            Log.e("CameraRepository", "Setting error state: $error")

            _cameraState.value = _cameraState.value.copy(
                isInitialized = false,
                error = error
            )

            Result.failure(CameraException.InitializationFailed(e.message ?: "Unknown error"))
        }
    }

    private fun setupCamera(
        surfaceProvider: Preview.SurfaceProvider,
        lifecycleOwner: LifecycleOwner
    ){
        Log.d("CameraRepository", "setupCamera started")
        val lensFacing = when (_cameraState.value.lensFacing){
            CameraLensFacing.FRONT -> CameraSelector.LENS_FACING_FRONT
            CameraLensFacing.BACK -> CameraSelector.LENS_FACING_BACK
        }
        Log.d("CameraRepository", "Using lens facing: $lensFacing")
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
        Log.d("CameraRepository", "Camera selector built")
        // Preview
        Log.d("CameraRepository", "Building preview...")
        preview = Preview.Builder().build().also {
            it.setSurfaceProvider(surfaceProvider)
        }
        Log.d("CameraRepository", "Preview built and surface provider set")

        // ImageCapture
        Log.d("CameraRepository", "Building image capture...")
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
        Log.d("CameraRepository", "Image capture built")

        // VideoCapture
        Log.d("CameraRepository", "Building video capture...")
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        videoCapture = VideoCapture.withOutput(recorder)
        Log.d("CameraRepository", "Video capture built")

        try {
            Log.d("CameraRepository", "Unbinding all use cases...")
            cameraProvider?.unbindAll()
            Log.d("CameraRepository", "Binding to lifecycle...")
            camera = cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                videoCapture
            )
            Log.d("CameraRepository", "Successfully bound to lifecycle")

            updateFlashMode()
            Log.d("CameraRepository", "Flash mode updated")
            Log.d("CameraRepository", "setupCamera completed successfully")

        } catch (e: Exception) {
            Log.e("CameraRepository", "Use case binding failed", e)
            _cameraState.value = _cameraState.value.copy(error = "Camera binding failed: ${e.message}")
            throw e
        }

    }

    private fun updateFlashMode() {
        camera?.cameraControl?.enableTorch(_cameraState.value.isFlashEnabled)
    }

    override suspend fun capturePhoto(): Result<Uri> {

        val imageCapture = imageCapture ?: return Result.failure(
            CameraException.PhotoCaptureFailed("Camera not initialized")
        )

        return try {
            val name = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault())
                .format(System.currentTimeMillis())

            // Create temporal file in internal cache

            val cacheDir = File(context.cacheDir,"temp_images")
            if(!cacheDir.exists()){
                cacheDir.mkdirs()
            }
            val tempImageFile = File(cacheDir, "$name.jpg")

            val outputOptions = ImageCapture.OutputFileOptions.Builder(tempImageFile).build()

//            Create file in device storage
//            val contentValues = ContentValues().apply {
//                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Running-Image")
//                }
//            }
//
//            val outputOptions = ImageCapture.OutputFileOptions.Builder(
//                context.contentResolver,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues
//            ).build()

            // Suspend function wrapper for callback
            val capturedUri: Uri = kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
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

                            // Create URI from temporal file
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                tempImageFile
                            )
//                            // Create URI from saved file
//                            val uri = output.savedUri
                            if (uri != null) {
                                Toast.makeText(context,"Photo capture succeeded: $uri",Toast.LENGTH_LONG).show()
                                _cameraState.value = _cameraState.value.copy(
                                    lastCapturedPhotoUri = uri.toString(),
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
            val name = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault())
                .format(System.currentTimeMillis())

            val cacheDir = File(context.cacheDir,"temp_videos")
            if(!cacheDir.exists()){
                cacheDir.mkdirs()
            }

            val tempVideoFile = File(cacheDir,"$name.mp4")

            val outputOptions = androidx.camera.video.FileOutputOptions.Builder(tempVideoFile).build()

//            val contentValues = ContentValues().apply {
//                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                    put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Running-Video")
//                }
//            }
//
//            val outputOptions = MediaStoreOutputOptions.Builder(
//                context.contentResolver,
//                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//            ).setContentValues(contentValues).build()

            recording = videoCapture.output
                .prepareRecording(context, outputOptions)
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
                            handleRecordingFinalized(recordEvent, tempVideoFile)
                        }
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(CameraException.VideoRecordingFailed(e.message ?: "Unknown error"))
        }
    }

    private fun handleRecordingFinalized(recordEvent: VideoRecordEvent.Finalize, tempVideoFile: File) {
        if (!recordEvent.hasError()) {

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempVideoFile
            )

//            val uri = recordEvent.outputResults.outputUri
            Toast.makeText(context,"Video capture succeeded: $uri",Toast.LENGTH_LONG).show()

            _cameraState.value = _cameraState.value.copy(
                isRecording = false,
                isPaused = false,
                lastRecordedVideoUri = uri.toString(),
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

    override suspend fun stopVideoRecording(): Result<Uri> {
        return try {
            val currentRecording = recording
            if (currentRecording != null) {
                currentRecording.stop()
                recording = null
                val uri : Uri = kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
                    val originalState = _cameraState.value
                    repositoryScope.launch {
                        _cameraState.collect{ state ->
                            if(!state.isRecording && state.lastRecordedVideoUri != originalState.lastRecordedVideoUri){
                                if(state.error != null){
                                    continuation.resumeWithException(
                                        CameraException.VideoRecordingFailed(state.error)
                                    )
                                } else {
                                    continuation.resume(Uri.parse(state.lastRecordedVideoUri))
                                }
                            }
                        }
                    }
                }


                Result.success(uri)
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