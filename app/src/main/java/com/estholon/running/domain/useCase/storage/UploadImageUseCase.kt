package com.estholon.running.domain.useCase.storage

import android.net.Uri
import com.estholon.running.domain.repository.StorageRepository
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val storageRepository: StorageRepository
) {

    operator fun invoke(runId: String, uri: Uri) {
        storageRepository.uploadImage(runId,uri)
    }

}