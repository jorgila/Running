package com.estholon.running.domain.useCase.camera

import com.estholon.running.domain.repository.CameraRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class SwitchCameraResultUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) : BaseSuspendResultUseCase<SwitchCameraResultUseCase.Params,Unit>() {

    data class Params(
        val surfaceProvider: Any,
        val lifecycleOwner: Any
    )

    override suspend fun execute(parameters: Params) {
        val result = cameraRepository.switchCamera(parameters.surfaceProvider,parameters.lifecycleOwner)
        result.getOrThrow()
    }

}