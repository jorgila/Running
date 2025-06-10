package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCaseNoParams
import javax.inject.Inject

class GetDistanceRecordResultUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) : BaseSuspendResultUseCaseNoParams<Double>(){

    override suspend fun execute() : Double {
        val result = runningRepository.getDistanceRecord()
        return result.getOrThrow()
    }

}