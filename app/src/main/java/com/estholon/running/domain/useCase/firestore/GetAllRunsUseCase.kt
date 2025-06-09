package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseFlowUseCase
import com.estholon.running.domain.useCase.BaseFlowUseCaseNoParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllRunsUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) : BaseFlowUseCaseNoParams<List<RunModel>>(){

    override fun execute(): Flow<List<RunModel>> {
        return runningRepository.getAllRuns()
    }

}