package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.TotalModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseFlowUseCaseNoParams
import com.estholon.running.domain.useCase.BaseSuspendUseCaseNoParams
import javax.inject.Inject

class GetSpeedRecordUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) : BaseSuspendUseCaseNoParams<Double>() {

    override suspend fun execute(): Double {
        val result = runningRepository.getSpeedRecord()
        return result.getOrThrow()
    }
}