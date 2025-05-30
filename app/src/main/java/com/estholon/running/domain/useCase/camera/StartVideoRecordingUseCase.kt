package com.estholon.running.domain.useCase.camera

import com.estholon.running.domain.exception.CameraException
import com.estholon.running.domain.repository.CameraRepository
import javax.inject.Inject

class StartVideoRecordingUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) {

    suspend operator fun invoke() : Result<Unit> {
        return try {
            cameraRepository.startVideoRecording()
        } catch (e: Exception) {
            Result.failure(CameraException.VideoRecordingFailed(e.message ?: "Unknown error"))
        }
    }

}