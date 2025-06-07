package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.TotalModel
import com.estholon.running.domain.repository.RunningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTotalsUseCase @Inject constructor(
    val runningRepository: RunningRepository
) {

    suspend operator fun invoke() : Flow<Result<TotalModel>> {
        return runningRepository.getTotals()
            .map { total -> Result.success(total) }
            .catch { exception -> emit(Result.failure(exception)) }
    }

}