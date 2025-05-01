package com.estholon.running.domain.useCase.firestore

import com.estholon.running.data.dto.RunDTO
import com.estholon.running.data.dto.TotalDTO
import com.estholon.running.data.network.DatabaseRepository
import javax.inject.Inject

class SetRunUseCase @Inject constructor(
    val databaseRepository: DatabaseRepository
) {


    suspend operator fun invoke(
        id: String?,
        user: String?,
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
        goalDistance : Double,
        intervalDefault : Int,
        intervalRunDuration : String,
        intervalWalkDuration : String,
        rounds : Int,
    ){
        val dto = prepareDTO(
            id,
            user,
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
            goalDistance,
            intervalDefault,
            intervalRunDuration,
            intervalWalkDuration,
            rounds
        )
        if(dto!=null){
            databaseRepository.setRun(id,dto)
        }

    }

    private fun prepareDTO(
        id: String?,
        user: String?,
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
        goalDistance : Double,
        intervalDefault : Int,
        intervalRunDuration : String,
        intervalWalkDuration : String,
        rounds : Int,
    ) : RunDTO? {

        if(
            id == null ||
            user==null
        ) return null

        return try {
            RunDTO(
                user,
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
                goalDistance,
                intervalDefault,
                intervalRunDuration,
                intervalWalkDuration,
                rounds
            )
        } catch (e: Exception){
            null
        }
    }


}