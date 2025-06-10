package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.domain.repository.PreferencesRepository
import javax.inject.Inject

class PreferencesGetBooleanUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(
        key: String,
        defaultValue: Boolean = false
    ) : Boolean {
        return preferencesRepository.getBoolean(key,defaultValue)
    }

}