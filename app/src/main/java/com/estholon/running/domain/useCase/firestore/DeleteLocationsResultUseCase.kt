package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class DeleteLocationsResultUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) : BaseSuspendResultUseCase<DeleteLocationsResultUseCase.DeleteLocationsParams,Unit>() {

    data class DeleteLocationsParams(
        val runId: String
    )

    override suspend fun execute(params: DeleteLocationsParams) : Unit {
        val result = runningRepository.deleteLocations(params.runId)
        if(result.isFailure){
            throw result.exceptionOrNull() ?: RuntimeException("Unkown error deleting location")
        }
    }

}