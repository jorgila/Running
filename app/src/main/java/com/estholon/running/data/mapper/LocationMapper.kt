package com.estholon.running.data.mapper

import com.estholon.running.data.dto.LocationDto
import com.estholon.running.domain.model.LocationModel

class LocationMapper {

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

}