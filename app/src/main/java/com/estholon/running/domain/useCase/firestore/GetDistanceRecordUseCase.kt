package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.repository.RunningRepository
import javax.inject.Inject

class GetDistanceRecordUseCase @Inject constructor(
    private val runningRepository: RunningRepository
){

    suspend operator fun invoke() : Result<Double> {
        return runningRepository.getDistanceRecord()
    }

}