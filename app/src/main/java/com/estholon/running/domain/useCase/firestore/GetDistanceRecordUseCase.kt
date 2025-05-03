package com.estholon.running.domain.useCase.firestore

import com.estholon.running.data.network.DatabaseRepository
import javax.inject.Inject

class GetDistanceRecordUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
){

    suspend operator fun invoke(
        callback: (Boolean) -> Unit
    ) : Double {
        return databaseRepository.getDistanceRecord(
            callback = callback
        )
    }

}