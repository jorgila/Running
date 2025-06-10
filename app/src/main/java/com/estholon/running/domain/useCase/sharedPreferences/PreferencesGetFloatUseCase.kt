package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.domain.repository.PreferencesRepository
import javax.inject.Inject

class PreferencesGetFloatUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(
        key: String,
        defaultValue: Float = 0F
    ) : Float {
        return preferencesRepository.getFloat(key,defaultValue)
    }

}