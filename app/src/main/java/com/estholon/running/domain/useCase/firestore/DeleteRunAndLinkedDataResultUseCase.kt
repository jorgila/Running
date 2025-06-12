package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class DeleteRunAndLinkedDataResultUseCase @Inject constructor(
    private val runningRepository: RunningRepository,
) : BaseSuspendResultUseCase<DeleteRunAndLinkedDataResultUseCase.DeleteRunParams,Unit>(){

    data class DeleteRunParams(
        val runId: String
    )

    override suspend fun execute(
        parameters: DeleteRunParams,
    ) : Unit {
        val result = runningRepository.deleteRun(parameters.runId)
        if(result.isFailure){
            throw result.exceptionOrNull() ?: RuntimeException("Unkown error deleting run")
        }
    }

}