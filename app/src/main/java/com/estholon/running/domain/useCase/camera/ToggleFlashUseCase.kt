package com.estholon.running.domain.useCase.camera

import com.estholon.running.domain.exception.CameraException
import com.estholon.running.domain.repository.CameraRepository
import javax.inject.Inject

class ToggleFlashUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) {

    suspend operator fun invoke() : Result<Unit> {
        return try {
            cameraRepository.toggleFlash()
        } catch (e: Exception){
            Result.failure(CameraException.FlashToggleFailed(e.message ?: "Unknown error"))
        }
    }

}