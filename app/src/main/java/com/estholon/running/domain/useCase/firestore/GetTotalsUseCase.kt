package com.estholon.running.domain.useCase.firestore

import com.estholon.running.data.network.DatabaseRepository
import com.estholon.running.domain.model.TotalModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalsUseCase @Inject constructor(
    val databaseRepository: DatabaseRepository
) {

    suspend fun getTotals() : Flow<TotalModel> {
        return databaseRepository.getTotals()
    }

}