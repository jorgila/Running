package com.estholon.running.domain.useCase.firestore

import com.estholon.running.data.network.DatabaseRepository
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