package com.estholon.running.domain.useCase.firestore

import com.estholon.running.data.network.DatabaseRepository
import javax.inject.Inject

class GetSpeedRecordUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {

    suspend operator fun invoke(
        callback: (Boolean, Double) -> Unit
    ) {
        databaseRepository.getSpeedRecord(callback)
    }

}