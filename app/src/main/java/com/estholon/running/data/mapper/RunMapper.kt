package com.estholon.running.data.mapper

import com.estholon.running.data.dto.RunDto
import com.estholon.running.data.network.response.RunResponse
import com.estholon.running.domain.model.RunModel
import javax.inject.Inject

class RunMapper @Inject constructor(

) {
    fun runResponseToDto(response: RunResponse): RunDto? {
        return if (isValidRunResponse(response)) {
            RunDto(
                user = response.user!!,
                runId = response.runId!!,
                startDate = response.startDate!!,
                startTime = response.startTime!!,
                kpiDuration = response.kpiDuration ?: "00:00:00",
                kpiDistance = response.kpiDistance ?: 0.0,
                kpiAvgSpeed = response.kpiAvgSpeed ?: 0.0,
                kpiMaxSpeed = response.kpiMaxSpeed ?: 0.0,
                kpiMinAltitude = response.kpiMinAltitude,
                kpiMaxAltitude = response.kpiMaxAltitude,
                goalDurationSelected = response.goalDurationSelected ?: false,
                goalHoursDefault = response.goalHoursDefault ?: 0,
                goalMinutesDefault = response.goalMinutesDefault ?: 0,
                goalSecondsDefault = response.goalSecondsDefault ?: 0,
                goalDistanceDefault = response.goalDistanceDefault ?: 0,
                intervalDefault = response.intervalDefault ?: 0,
                intervalRunDuration = response.intervalRunDuration ?: "00:00",
                intervalWalkDuration = response.intervalWalkDuration ?: "00:00",
                rounds = response.rounds ?: 1
            )
        } else null
    }

    fun runDtoToDomain(dto: RunDto): RunModel {
        return RunModel(
            user = dto.user,
            runId = dto.runId,
            startDate = dto.startDate,
            startTime = dto.startTime,
            kpiDuration = dto.kpiDuration,
            kpiDistance = dto.kpiDistance,
            kpiAvgSpeed = dto.kpiAvgSpeed,
            kpiMaxSpeed = dto.kpiMaxSpeed,
            kpiMinAltitude = dto.kpiMinAltitude,
            kpiMaxAltitude = dto.kpiMaxAltitude,
            goalDurationSelected = dto.goalDurationSelected,
            goalHoursDefault = dto.goalHoursDefault,
            goalMinutesDefault = dto.goalMinutesDefault,
            goalSecondsDefault = dto.goalSecondsDefault,
            goalDistanceDefault = dto.goalDistanceDefault,
            intervalDefault = dto.intervalDefault,
            intervalRunDuration = dto.intervalRunDuration,
            intervalWalkDuration = dto.intervalWalkDuration,
            rounds = dto.rounds
        )
    }

    fun runDomainToDto(model: RunModel): RunDto {
        return RunDto(
            user = model.user ?: "",
            runId = model.runId,
            startDate = model.startDate ?: "",
            startTime = model.startTime ?: "",
            kpiDuration = model.kpiDuration,
            kpiDistance = model.kpiDistance,
            kpiAvgSpeed = model.kpiAvgSpeed,
            kpiMaxSpeed = model.kpiMaxSpeed,
            kpiMinAltitude = model.kpiMinAltitude,
            kpiMaxAltitude = model.kpiMaxAltitude,
            goalDurationSelected = model.goalDurationSelected,
            goalHoursDefault = model.goalHoursDefault,
            goalMinutesDefault = model.goalMinutesDefault,
            goalSecondsDefault = model.goalSecondsDefault,
            goalDistanceDefault = model.goalDistanceDefault,
            intervalDefault = model.intervalDefault,
            intervalRunDuration = model.intervalRunDuration,
            intervalWalkDuration = model.intervalWalkDuration,
            rounds = model.rounds
        )
    }

    private fun isValidRunResponse(response: RunResponse): Boolean {
        return response.user != null &&
                response.runId != null &&
                response.startDate != null &&
                response.startTime != null &&
                response.kpiDuration != "00:00:00"
    }
}