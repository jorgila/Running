package com.estholon.running.domain.repository

import android.net.Uri
import com.estholon.running.domain.model.CameraModel
import kotlinx.coroutines.flow.StateFlow

interface CameraRepository {

    val cameraState: StateFlow<CameraModel>
    suspend fun initializeCamera(surfaceProvider: Any, lifecycleOwner: Any) : Result<Unit>
    suspend fun capturePhoto() : Result<Uri>
    suspend fun startVideoRecording() : Result<Unit>
    suspend fun stopVideoRecording() : Result<Uri>
    suspend fun pauseVideoRecording() : Result<Unit>
    suspend fun resumeVideoRecording() : Result<Unit>
    suspend fun switchCamera(surfaceProvider: Any, lifecycleOwner: Any) : Result<Unit>
    suspend fun toggleFlash() : Result<Unit>
    fun clearError()
    fun cleanup()

}