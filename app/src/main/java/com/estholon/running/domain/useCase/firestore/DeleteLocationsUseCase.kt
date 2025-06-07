package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.repository.RunningRepository
import javax.inject.Inject

class DeleteLocationsUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) {

    suspend operator fun invoke(id: String) : Result<Unit>{
        return runningRepository.deleteLocations(id)
    }

}