package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseFlowUseCase
import com.estholon.running.domain.useCase.BaseFlowUseCaseNoParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRunUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) : BaseFlowUseCase<GetRunUseCase.Params,RunModel>() {

    data class Params(
        val id: String
    )

    override fun execute(parameters: Params) : Flow<RunModel> {
        return runningRepository.getRun(parameters.id)
    }

}