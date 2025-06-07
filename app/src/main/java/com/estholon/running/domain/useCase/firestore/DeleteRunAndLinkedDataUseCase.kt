package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.repository.RunningRepository
import javax.inject.Inject

class DeleteRunAndLinkedDataUseCase @Inject constructor(
    private val runningRepository: RunningRepository,
) {

    suspend operator fun invoke(
        id: String,
    ) : Result<Unit> {
        return runningRepository.deleteRun(id)
    }

}