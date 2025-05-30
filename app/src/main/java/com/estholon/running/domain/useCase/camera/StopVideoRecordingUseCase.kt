package com.estholon.running.domain.useCase.camera

import com.estholon.running.domain.exception.CameraException
import com.estholon.running.domain.repository.CameraRepository
import javax.inject.Inject

class StopVideoRecordingUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) {

    suspend operator fun invoke() : Result<String>{
        return try {
            cameraRepository.stopVideoRecording()
        } catch (e: Exception) {
            Result.failure(CameraException.VideoRecordingFailed(e.message ?: "Unknown error"))
        }
    }

}