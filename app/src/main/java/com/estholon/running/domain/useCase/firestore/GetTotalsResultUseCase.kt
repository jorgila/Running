package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.TotalModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseFlowResultUseCaseNoParams
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalsResultUseCase @Inject constructor(
    val runningRepository: RunningRepository
) : BaseFlowResultUseCaseNoParams<TotalModel>(){

    override fun execute() : Flow<TotalModel> {
        return runningRepository.getTotals()
    }

}