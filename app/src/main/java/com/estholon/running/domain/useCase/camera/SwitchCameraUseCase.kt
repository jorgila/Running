package com.estholon.running.domain.useCase.camera

import androidx.camera.core.Preview.SurfaceProvider
import androidx.lifecycle.LifecycleOwner
import com.estholon.running.domain.exception.CameraException
import com.estholon.running.domain.repository.CameraRepository
import javax.inject.Inject

class SwitchCameraUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) {

    suspend operator fun invoke(
        surfaceProvider: Any,
        lifecycleOwner: Any
    ) : Result<Unit> {
        return try {
            cameraRepository.switchCamera(surfaceProvider, lifecycleOwner)
        } catch (e: Exception) {
            Result.failure(CameraException.CameraSwitchFailed(e.message ?: "Unknown error"))
        }
    }

}