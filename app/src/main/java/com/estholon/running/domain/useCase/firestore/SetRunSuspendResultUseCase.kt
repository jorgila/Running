package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SetRunSuspendResultUseCase @Inject constructor(
    private val runningRepository: RunningRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseSuspendResultUseCase<SetRunSuspendResultUseCase.Params, Unit>(dispatcher) {

    data class Params(
        val model: RunModel
    )

    override suspend fun execute(parameters: Params) {

        val result = runningRepository.setRun(parameters.model)
        if(result.isFailure){
            throw  result.exceptionOrNull() ?: RuntimeException("Unknown error setting run")
        }
    }

}