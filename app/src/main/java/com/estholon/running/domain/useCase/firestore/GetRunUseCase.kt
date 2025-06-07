package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.repository.RunningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRunUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) {

    operator fun invoke(id: String) : Flow<Result<RunModel>> {
        return runningRepository.getRun(id)
            .map { run -> Result.success(run) }
            .catch { exception -> emit(Result.failure(exception)) }
    }

}