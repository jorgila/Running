package com.estholon.running.domain.repository

import androidx.lifecycle.LifecycleOwner
import com.estholon.running.domain.model.CameraModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CameraRepository {

    val cameraState: StateFlow<CameraModel>
    suspend fun initializeCamera(surfaceProvider: Any, lifecycleOwner: Any) : Result<Unit>
    suspend fun capturePhoto() : Result<String>
    suspend fun startVideoRecording() : Result<Unit>
    suspend fun stopVideoRecording() : Result<String>
    suspend fun pauseVideoRecording() : Result<Unit>
    suspend fun resumeVideoRecording() : Result<Unit>
    suspend fun switchCamera(surfaceProvider: Any, lifecycleOwner: Any) : Result<Unit>
    suspend fun toggleFlash() : Result<Unit>
    fun clearError()
    fun cleanup()

}