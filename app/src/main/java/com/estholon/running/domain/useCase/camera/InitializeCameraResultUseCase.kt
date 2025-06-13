package com.estholon.running.domain.useCase.camera

import com.estholon.running.domain.model.AudioProgress
import com.estholon.running.domain.repository.CameraRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class InitializeCameraResultUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) : BaseSuspendResultUseCase<InitializeCameraResultUseCase.InitializeCameraParams,Unit>(){

    data class InitializeCameraParams(
        val surfaceProvider: Any,
        val lifecycleOwner: Any
    )

    override suspend fun execute(parameters: InitializeCameraParams) {
        val result = cameraRepository.initializeCamera(parameters.surfaceProvider,parameters.lifecycleOwner)
        result.getOrThrow()
    }

}