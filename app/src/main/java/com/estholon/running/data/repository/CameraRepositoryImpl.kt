package com.estholon.running.data.repository

import android.content.Context
import com.estholon.running.domain.model.CameraModel
import com.estholon.running.domain.repository.CameraRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import javax.inject.Inject

class CameraRepositoryImpl @Inject constructor(
    private val context: Context
) : CameraRepository {

    private val _cameraState = MutableStateFlow(CameraModel())
    override val cameraState: StateFlow<CameraModel> = _cameraState.asStateFlow()

    override suspend fun initializeCamera(surfaceProvider: Any, lifecycleOwner: Any): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun capturePhoto(): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun startVideoRecording(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun stopVideoRecording(): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun pauseVideoRecording(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun resumeVideoRecording(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun switchCamera(surfaceProvider: Any, lifecycleOwner: Any): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleFlash(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun clearError() {
        TODO("Not yet implemented")
    }

    override fun cleanup() {
        TODO("Not yet implemented")
    }
}