package com.estholon.running.domain.useCase.camera

import androidx.lifecycle.LifecycleOwner
import com.estholon.running.domain.exception.CameraException
import com.estholon.running.domain.repository.CameraRepository
import javax.inject.Inject

class InitializeCameraUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) {

    suspend operator fun invoke(
        surfaceProvider: Any,
        lifecycleOwner: Any
    ) : Result<Unit> {
        return try {
            cameraRepository.initializeCamera(surfaceProvider, lifecycleOwner)
        } catch (e: Exception){
            Result.failure(CameraException.InitializationFailed(e.message ?: "Unknown error"))
        }
    }

}