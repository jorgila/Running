package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseSuspendUseCase
import javax.inject.Inject

class DeleteRunAndLinkedDataUseCase @Inject constructor(
    private val runningRepository: RunningRepository,
) : BaseSuspendUseCase<DeleteRunAndLinkedDataUseCase.Params,Unit>(){

    data class Params(
        val runId: String
    )

    override suspend fun execute(
        parameters: Params,
    ) : Unit {
        val result = runningRepository.deleteRun(parameters.runId)
        if(result.isFailure){
            throw result.exceptionOrNull() ?: RuntimeException("Unkown error deleting run")
        }
    }

}