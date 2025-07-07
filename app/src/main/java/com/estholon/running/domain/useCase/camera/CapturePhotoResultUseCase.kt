package com.estholon.running.domain.useCase.camera

import android.net.Uri
import com.estholon.running.domain.repository.CameraRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCaseNoParams
import javax.inject.Inject

class CapturePhotoResultUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) : BaseSuspendResultUseCaseNoParams<Uri>() {

    override suspend fun execute(): Uri {
        val result = cameraRepository.capturePhoto()
        return result.getOrThrow()
    }

}