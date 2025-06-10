package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.domain.repository.PreferencesRepository
import javax.inject.Inject

class PreferencesGetIntUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(
        key: String,
        defaultValue: Int = 0
    ) : Int {
        return preferencesRepository.getInt(key,defaultValue)
    }

}