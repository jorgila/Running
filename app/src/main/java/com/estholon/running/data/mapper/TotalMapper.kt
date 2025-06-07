package com.estholon.running.data.mapper

import com.estholon.running.data.dto.RunDTO
import com.estholon.running.data.dto.TotalDTO
import com.estholon.running.data.network.response.TotalResponse
import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.model.TotalModel
import javax.inject.Inject

class TotalMapper @Inject constructor(

) {

    fun totalResponseToDto(response: TotalResponse): TotalDTO? {
        return if (isValidTotalResponse(response)) {
            TotalDTO(
                recordAvgSpeed = response.recordAvgSpeed!!,
                recordDistance = response.recordDistance!!,
                recordSpeed = response.recordSpeed!!,
                totalDistance = response.totalDistance!!,
                totalRuns = response.totalRuns!!,
                totalTime = response.totalTime!!
            )
        } else null
    }

    fun totalDtoToDomain(dto: TotalDTO): TotalModel {
        return TotalModel(
            recordAvgSpeed = dto.recordAvgSpeed,
            recordDistance = dto.recordDistance,
            recordSpeed = dto.recordSpeed,
            totalDistance = dto.totalDistance,
            totalRuns = dto.totalRuns,
            totalTime = dto.totalTime
        )
    }

    fun totalDomainToDto(model: TotalModel): TotalDTO {
        return TotalDTO(
            recordAvgSpeed = model.recordAvgSpeed,
            recordDistance = model.recordDistance,
            recordSpeed = model.recordSpeed,
            totalDistance = model.totalDistance,
            totalRuns = model.totalRuns,
            totalTime = model.totalTime
        )

    }

    private fun isValidTotalResponse(response: TotalResponse): Boolean {
        return response.recordAvgSpeed != null &&
                response.recordDistance != null &&
                response.recordSpeed != null &&
                response.totalDistance != null &&
                response.totalRuns != null &&
                response.totalTime != null
    }

}