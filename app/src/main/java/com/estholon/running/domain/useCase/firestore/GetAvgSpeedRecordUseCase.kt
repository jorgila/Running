package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseSuspendUseCaseNoParams
import javax.inject.Inject

class GetAvgSpeedRecordUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) : BaseSuspendUseCaseNoParams<Double>(){

    override suspend fun execute() : Double {
        val result = runningRepository.getAvgSpeedRecord()
        return result.getOrThrow()
    }

}