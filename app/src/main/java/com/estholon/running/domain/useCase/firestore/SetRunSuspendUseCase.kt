package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseSuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SetRunSuspendUseCase @Inject constructor(
    private val runningRepository: RunningRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseSuspendUseCase<SetRunSuspendUseCase.Params, Unit>(dispatcher) {

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