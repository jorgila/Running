package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseFlowResultUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRunResultUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) : BaseFlowResultUseCase<GetRunResultUseCase.Params,RunModel>() {

    data class Params(
        val id: String
    )

    override fun execute(parameters: Params) : Flow<RunModel> {
        return runningRepository.getRun(parameters.id)
    }

}