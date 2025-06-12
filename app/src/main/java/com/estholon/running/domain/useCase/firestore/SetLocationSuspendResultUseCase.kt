package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.LocationModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SetLocationSuspendResultUseCase @Inject constructor(
    private val runningRepository: RunningRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseSuspendResultUseCase<SetLocationSuspendResultUseCase.SetLocationParams, Unit>(dispatcher) {

    data class SetLocationParams(
        val runId: String,
        val documentName: String,
        val model: LocationModel
    )

    override suspend fun execute(parameters: SetLocationParams) {

        val result = runningRepository.setLocation(
            parameters.runId,
            parameters.documentName,
            parameters.model
        )
        if(result.isFailure){
            throw  result.exceptionOrNull() ?: RuntimeException("Unknown error setting location")
        }

    }

}