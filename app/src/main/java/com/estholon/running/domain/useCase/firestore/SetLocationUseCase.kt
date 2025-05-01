package com.estholon.running.domain.useCase.firestore

import com.estholon.running.data.dto.LocationDTO
import com.estholon.running.data.network.DatabaseRepository
import javax.inject.Inject

class SetLocationUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {

    suspend operator fun invoke(
        id: String?,
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
    ){
        val dto = prepareDTO(
            id,
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
        if(dto!=null){
            databaseRepository.setLocation(id,docName,dto)
        }
    }

    private fun prepareDTO(
        id: String?,
        time : String,
        latitude : Double,
        longitude : Double,
        altitude : Double,
        hasAltitude : Boolean,
        speedFromGoogle : Float,
        speedFromApp : Double,
        isMaxSpeed : Boolean,
        isRunInterval : Boolean
    ) : LocationDTO? {
        if(
            id==null ||
            time==null ||
            latitude == null ||
            longitude == null
        ) return null
        return try {
            LocationDTO(
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
        } catch (e: Exception){
            null
        }
    }

}