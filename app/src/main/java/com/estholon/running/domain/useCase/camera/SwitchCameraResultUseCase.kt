package com.estholon.running.domain.useCase.camera

import com.estholon.running.domain.repository.CameraRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class SwitchCameraResultUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) : BaseSuspendResultUseCase<SwitchCameraResultUseCase.SwitchCameraParams,Unit>() {

    data class SwitchCameraParams(
        val surfaceProvider: Any,
        val lifecycleOwner: Any
    )

    override suspend fun execute(parameters: SwitchCameraParams) {
        val result = cameraRepository.switchCamera(parameters.surfaceProvider,parameters.lifecycleOwner)
        result.getOrThrow()
    }

}