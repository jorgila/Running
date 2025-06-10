package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.TotalModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SetTotalsSuspendResultUseCase @Inject constructor(
    private val runningRepository: RunningRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseSuspendResultUseCase<SetTotalsSuspendResultUseCase.Params, Unit>(dispatcher){

    data class Params(val model: TotalModel)

    override suspend fun execute(parameters: Params) {

        val result = runningRepository.setTotals(parameters.model)
        if (result.isFailure){
            throw result.exceptionOrNull() ?: RuntimeException("Unkown error setting total")
        }
    }

}