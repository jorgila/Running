package com.estholon.running.domain.useCase.storage

import android.net.Uri
import com.estholon.running.domain.repository.StorageRepository
import javax.inject.Inject

class DeleteVideosUseCase@Inject constructor(
    private val storageRepository: StorageRepository
) {

    suspend operator fun invoke(runId: String) : Boolean {
        return storageRepository.deleteVideos(runId)
    }
}