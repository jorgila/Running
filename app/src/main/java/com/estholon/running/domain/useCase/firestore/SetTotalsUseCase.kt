package com.estholon.running.domain.useCase.firestore

import androidx.lifecycle.viewModelScope
import com.estholon.running.data.dto.TotalDTO
import com.estholon.running.domain.model.TotalModel
import com.estholon.running.domain.repository.RunningRepository
import com.estholon.running.domain.useCase.BaseUseCase
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SetTotalsUseCase @Inject constructor(
    private val runningRepository: RunningRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseUseCase<SetTotalsUseCase.Params, Unit>(dispatcher){

    data class Params(val model: TotalModel)

    override suspend fun execute(parameters: Params) {

        val result = runningRepository.setTotals(parameters.model)
        if (result.isFailure){
            throw result.exceptionOrNull() ?: RuntimeException("Unkown error setting total")
        }
    }

}