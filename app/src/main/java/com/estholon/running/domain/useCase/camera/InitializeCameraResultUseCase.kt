package com.estholon.running.domain.useCase.camera

import com.estholon.running.domain.repository.CameraRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class InitializeCameraResultUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) : BaseSuspendResultUseCase<InitializeCameraResultUseCase.Params,Unit>(){

    data class Params(
        val surfaceProvider: Any,
        val lifecycleOwner: Any
    )

    override suspend fun execute(parameters: Params) {
        val result = cameraRepository.initializeCamera(parameters.surfaceProvider,parameters.lifecycleOwner)
        result.getOrThrow()
    }

}