package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseFlowResultUseCaseNoParams
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllRunsResultUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) : BaseFlowResultUseCaseNoParams<List<RunModel>>(){

    override fun execute(): Flow<List<RunModel>> {
        return runningRepository.getAllRuns()
    }

}