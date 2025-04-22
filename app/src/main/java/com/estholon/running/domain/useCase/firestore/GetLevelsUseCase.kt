package com.estholon.running.domain.useCase.firestore

import com.estholon.running.data.network.DatabaseRepository
import com.estholon.running.domain.model.LevelModel
import com.estholon.running.domain.model.TotalModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLevelsUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {

    operator fun invoke() : Flow<List<LevelModel>> {
        return databaseRepository.getLevels()
    }

}