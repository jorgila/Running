package com.estholon.running.domain.useCase.storage

import android.net.Uri
import com.estholon.running.domain.repository.StorageRepository
import javax.inject.Inject

class DownloadImagesUseCase @Inject constructor(
    private val storageRepository: StorageRepository
) {

    suspend operator fun invoke(runId: String) : List<Uri> {
        return storageRepository.downloadImages(runId)
    }

}