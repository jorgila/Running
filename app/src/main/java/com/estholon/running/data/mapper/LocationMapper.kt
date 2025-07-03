package com.estholon.running.data.mapper

import com.estholon.running.data.dto.LocationDto
import com.estholon.running.data.dto.RunDto
import com.estholon.running.data.network.response.RunResponse
import com.estholon.running.data.response.LocationResponse
import com.estholon.running.domain.model.LocationModel
import com.estholon.running.domain.model.RunModel

class LocationMapper {

    fun locationResponseToDto(response: LocationResponse): LocationDto? {
        return if (isValidLocationResponse(response)) {
            LocationDto(
                time = response.time,
                latitude = response.latitude,
                longitude = response.longitude,
                altitude = response.altitude,
                hasAltitude = response.hasAltitude,
                speedFromGoogle = response.speedFromGoogle,
                speedFromApp = response.speedFromApp,
                isMaxSpeed = response.isMaxSpeed,
                isRunInterval = response.isRunInterval
            )
        } else null
    }

    fun locationDtoToDomain(dto: LocationDto): LocationModel {
        return LocationModel(
            time = dto.time,
            latitude = dto.latitude,
            longitude = dto.longitude,
            altitude = dto.altitude,
            hasAltitude = dto.hasAltitude,
            speedFromGoogle = dto.speedFromGoogle,
            speedFromApp = dto.speedFromApp,
            isMaxSpeed = dto.isMaxSpeed,
            isRunInterval = dto.isRunInterval
        )
    }

    fun locationDomainToDto(model: LocationModel): LocationDto {
        return LocationDto(
            time = model.time,
            latitude = model.latitude,
            longitude = model.longitude,
            altitude = model.altitude,
            hasAltitude = model.hasAltitude,
            speedFromGoogle = model.speedFromGoogle,
            speedFromApp = model.speedFromApp,
            isMaxSpeed = model.isMaxSpeed,
            isRunInterval = model.isRunInterval
        )
    }

    private fun isValidLocationResponse(response: LocationResponse): Boolean {
        return response.time != "00:00:00"
    }

}