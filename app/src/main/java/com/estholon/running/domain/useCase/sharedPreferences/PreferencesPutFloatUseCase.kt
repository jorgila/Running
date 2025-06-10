package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.domain.repository.PreferencesRepository
import javax.inject.Inject

class PreferencesPutFloatUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(
        key: String,
        value: Float
    ){
        preferencesRepository.putFloat(key,value)
    }

}