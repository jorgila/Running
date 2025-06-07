package com.estholon.running.domain.useCase.firestore

import androidx.lifecycle.viewModelScope
import com.estholon.running.data.dto.TotalDTO
import com.estholon.running.domain.model.TotalModel
import com.estholon.running.domain.repository.RunningRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import javax.inject.Inject

class SetTotalsUseCase @Inject constructor(
    val runningRepository: RunningRepository
) {

    suspend operator fun invoke(
        recordAvgSpeed : Double,
        recordDistance : Double,
        recordSpeed : Double,
        totalDistance : Double,
        totalRuns : Double,
        totalTime : Double
    ) : Result<Unit> {

        val model = TotalModel(
            recordAvgSpeed,
            recordDistance,
            recordSpeed,
            totalDistance,
            totalRuns,
            totalTime
        )

        return runningRepository.setTotals(model)

    }

}