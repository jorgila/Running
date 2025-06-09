package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseSuspendUseCaseNoParams
import javax.inject.Inject

class GetDistanceRecordUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) : BaseSuspendUseCaseNoParams<Double>(){

    override suspend fun execute() : Double {
        val result = runningRepository.getDistanceRecord()
        return result.getOrThrow()
    }

}