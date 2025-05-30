package com.estholon.running.domain.useCase.camera

import com.estholon.running.domain.repository.CameraRepository
import javax.inject.Inject

class ClearErrorUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) {

    operator fun invoke() {
        cameraRepository.clearError()
    }

}