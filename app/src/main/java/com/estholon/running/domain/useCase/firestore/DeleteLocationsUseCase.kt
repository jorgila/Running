package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseSuspendUseCase
import javax.inject.Inject

class DeleteLocationsUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) : BaseSuspendUseCase<DeleteLocationsUseCase.Params,Unit>() {

    data class Params(
        val runId: String
    )

    override suspend fun execute(params: Params) : Unit {
        val result = runningRepository.deleteLocations(params.runId)
        if(result.isFailure){
            throw result.exceptionOrNull() ?: RuntimeException("Unkown error deleting location")
        }
    }

}