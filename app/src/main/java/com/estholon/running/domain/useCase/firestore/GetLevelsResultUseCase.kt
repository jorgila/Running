package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.LevelModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseFlowResultUseCaseNoParams
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLevelsResultUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) : BaseFlowResultUseCaseNoParams<List<LevelModel>>(){

    override fun execute(): Flow<List<LevelModel>> {
        return runningRepository.getLevels()
    }

}