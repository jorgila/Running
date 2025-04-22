package com.estholon.running.domain.useCase.firestore

import androidx.lifecycle.viewModelScope
import com.estholon.running.data.dto.TotalDTO
import com.estholon.running.data.network.DatabaseRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import javax.inject.Inject

class SetTotalsUseCase @Inject constructor(
    val databaseRepository: DatabaseRepository
) {

    suspend operator fun invoke(
        recordAvgSpeed : Double,
        recordDistance : Double,
        recordSpeed : Double,
        totalDistance : Double,
        totalRuns : Double,
        totalTime : Double
    ){
        val dto = prepareDTO(
            recordAvgSpeed,
            recordDistance,
            recordSpeed,
            totalDistance,
            totalRuns,
            totalTime
        )
        if(dto!=null){
            databaseRepository.setTotals(dto)
        }

    }

    fun prepareDTO(
        recordAvgSpeed : Double,
        recordDistance : Double,
        recordSpeed : Double,
        totalDistance : Double,
        totalRuns : Double,
        totalTime : Double
    ) : TotalDTO? {

        if(
            recordAvgSpeed==0.00 ||
            recordDistance ==0.00 ||
            recordSpeed == 0.00 ||
            totalDistance == 0.00 ||
            totalRuns == 0.00 ||
            totalTime == 0.00
        ) return null

        return try {
            TotalDTO(
                recordAvgSpeed,
                recordDistance,
                recordSpeed,
                totalDistance,
                totalRuns,
                totalTime
            )
        } catch (e: Exception){
            null
        }
    }

}