package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.LevelModel
import com.estholon.running.domain.model.TotalModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseFlowUseCaseNoParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLevelsUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) : BaseFlowUseCaseNoParams<List<LevelModel>>(){

    override fun execute(): Flow<List<LevelModel>> {
        return runningRepository.getLevels()
    }

}