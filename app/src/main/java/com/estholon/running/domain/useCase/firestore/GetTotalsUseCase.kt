package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.TotalModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseFlowUseCaseNoParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTotalsUseCase @Inject constructor(
    val runningRepository: RunningRepository
) : BaseFlowUseCaseNoParams<TotalModel>(){

    override fun execute() : Flow<TotalModel> {
        return runningRepository.getTotals()
    }

}