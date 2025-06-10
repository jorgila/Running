package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.domain.repository.PreferencesRepository
import javax.inject.Inject

class PreferencesResetUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke() : Boolean {
        preferencesRepository.resetPreferences()
        return true
    }

}