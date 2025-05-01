package com.estholon.running.domain.useCase.firestore

import androidx.compose.runtime.rememberCoroutineScope
import com.estholon.running.data.dto.TotalDTO
import com.estholon.running.data.network.DatabaseRepository
import com.estholon.running.domain.useCase.others.GetMilisecondsFromStringWithDHMS
import com.estholon.running.domain.useCase.others.GetSecondsFromWatchUseCase
import javax.inject.Inject

class DeleteRunAndLinkedDataUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
) {

    suspend operator fun invoke(
        id: String,
        callback: (Boolean) -> Unit
    ){
        databaseRepository.deleteRunAndLinkedData(
            id,
            callback
        )
    }

}