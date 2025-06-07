package com.estholon.running.data.mapper

import com.estholon.running.data.dto.LevelDTO
import com.estholon.running.data.dto.LocationDTO
import com.estholon.running.data.dto.RunDTO
import com.estholon.running.data.network.response.LevelResponse
import com.estholon.running.domain.model.LevelModel
import com.estholon.running.domain.model.LocationModel
import com.estholon.running.domain.model.RunModel

class LocationMapper {

    fun locationDomainToDto(model: LocationModel): LocationDTO {
        return LocationDTO(
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