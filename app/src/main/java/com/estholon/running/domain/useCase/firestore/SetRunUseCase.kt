package com.estholon.running.domain.useCase.firestore

import com.estholon.running.data.dto.RunDTO
import com.estholon.running.data.dto.TotalDTO
import com.estholon.running.domain.exception.RunningException
import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SetRunUseCase @Inject constructor(
    private val runningRepository: RunningRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseUseCase<SetRunUseCase.Params, Unit>(dispatcher) {

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