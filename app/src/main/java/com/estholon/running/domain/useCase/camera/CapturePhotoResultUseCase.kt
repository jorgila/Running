package com.estholon.running.domain.useCase.camera

import com.estholon.running.domain.repository.CameraRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCaseNoParams
import javax.inject.Inject

class CapturePhotoResultUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) : BaseSuspendResultUseCaseNoParams<String>() {

    override suspend fun execute(): String {
        val result = cameraRepository.capturePhoto()
        return result.getOrThrow()
    }

}