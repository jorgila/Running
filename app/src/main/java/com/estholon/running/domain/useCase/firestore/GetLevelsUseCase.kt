package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.LevelModel
import com.estholon.running.domain.model.TotalModel
import com.estholon.running.domain.repository.RunningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLevelsUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) {

    operator fun invoke() : Flow<Result<List<LevelModel>>> {
        return runningRepository.getLevels()
            .map { levels -> Result.success(levels) }
            .catch { exception -> emit(Result.failure(exception)) }
    }

}