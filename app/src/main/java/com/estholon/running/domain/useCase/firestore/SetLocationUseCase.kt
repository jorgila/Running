package com.estholon.running.domain.useCase.firestore

import com.estholon.running.data.dto.LocationDTO
import com.estholon.running.domain.model.LocationModel
import com.estholon.running.domain.repository.RunningRepository
import javax.inject.Inject

class SetLocationUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) {

    suspend operator fun invoke(
        runId: String,
        docName: String,
        time : String,
        latitude : Double,
        longitude : Double,
        altitude : Double,
        hasAltitude : Boolean,
        speedFromGoogle : Float,
        speedFromApp : Double,
        isMaxSpeed : Boolean,
        isRunInterval : Boolean
    ) : Result<Unit> {

        val model = LocationModel(
            time,
            latitude,
            longitude,
            altitude,
            hasAltitude,
            speedFromGoogle,
            speedFromApp,
            isMaxSpeed,
            isRunInterval
        )

        return runningRepository.setLocation(runId,docName,model)

    }

}