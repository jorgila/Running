package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.repository.RunningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllRunsUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) {

    operator fun invoke() : Flow<Result<List<RunModel>>> {
        return runningRepository.getAllRuns()
            .map { runs -> Result.success(runs) }
            .catch { exception -> emit(Result.failure(exception)) }
    }

}