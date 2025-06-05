package com.estholon.running.domain.useCase.firestore

import com.estholon.running.data.network.DatabaseRepository
import com.estholon.running.domain.model.RunModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllRunsUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {

    suspend operator fun invoke() : Flow<List<RunModel>> {
        return databaseRepository.getAllRuns()
    }

}