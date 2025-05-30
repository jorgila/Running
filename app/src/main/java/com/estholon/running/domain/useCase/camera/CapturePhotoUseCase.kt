package com.estholon.running.domain.useCase.camera

import com.estholon.running.domain.exception.CameraException
import com.estholon.running.domain.repository.CameraRepository
import javax.inject.Inject

class CapturePhotoUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) {

    suspend operator fun invoke() : Result<String> {
        return try {
            cameraRepository.capturePhoto()
        } catch (e: Exception){
            Result.failure(CameraException.PhotoCaptureFailed(e.message ?: "Unknown error"))
        }
    }

}