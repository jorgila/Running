package com.estholon.running.domain.useCase.firestore

import com.estholon.running.data.dto.RunDTO
import com.estholon.running.data.dto.TotalDTO
import com.estholon.running.domain.exception.RunningException
import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.repository.RunningRepository
import javax.inject.Inject

class SetRunUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) {


    suspend operator fun invoke(
        id: String,
        user: String,
        startDate : String,
        startTime : String,
        kpiDuration : String,
        kpiDistance : Double,
        kpiAvgSpeed : Double,
        kpiMaxSpeed : Double,
        kpiMinAltitude : Double?,
        kpiMaxAltitude : Double?,
        goalDurationSelected : Boolean,
        goalHoursDefault : Int,
        goalMinutesDefault: Int,
        goalSecondsDefault : Int,
        goalDistanceDefault : Int,
        intervalDefault : Int,
        intervalRunDuration : String,
        intervalWalkDuration : String,
        rounds : Int,
    ) : Result<Unit> {

        val model = RunModel(
            user,
            id,
            startDate,
            startTime,
            kpiDuration,
            kpiDistance,
            kpiAvgSpeed,
            kpiMaxSpeed,
            kpiMinAltitude,
            kpiMaxAltitude,
            goalDurationSelected,
            goalHoursDefault,
            goalMinutesDefault,
            goalSecondsDefault,
            goalDistanceDefault,
            intervalDefault,
            intervalRunDuration,
            intervalWalkDuration,
            rounds
        )

        return runningRepository.setRun(id,model)

    }

}