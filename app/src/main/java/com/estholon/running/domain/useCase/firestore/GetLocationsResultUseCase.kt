package com.estholon.running.domain.useCase.firestore

import com.estholon.running.domain.model.LevelModel
import com.estholon.running.domain.model.LocationModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseFlowResultUseCase
import com.estholon.running.domain.useCase.BaseFlowResultUseCaseNoParams
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationsResultUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) : BaseFlowResultUseCase<GetLocationsResultUseCase.GetLocationsParams,List<LocationModel>>(){

    data class GetLocationsParams(
        val runId: String
    )

    override fun execute(parameters: GetLocationsParams): Flow<List<LocationModel>> {
        return runningRepository.getLocations(parameters.runId)
    }

}