package com.estholon.running.domain.useCase.firestore

import com.estholon.running.data.network.DatabaseRepository
import javax.inject.Inject

class DeleteLocationsUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {

    operator fun invoke(id: String, callback: (Boolean) -> Unit){
        databaseRepository.deleteLocations(id,callback)
    }

}