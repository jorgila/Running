package com.estholon.running.domain.useCase.camera

import com.estholon.running.domain.repository.CameraRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCaseNoParams
import javax.inject.Inject

class ToggleFlashResultUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) : BaseSuspendResultUseCaseNoParams<Unit>() {

    override suspend fun execute() {
        val result = cameraRepository.toggleFlash()
        result.getOrThrow()
    }

}