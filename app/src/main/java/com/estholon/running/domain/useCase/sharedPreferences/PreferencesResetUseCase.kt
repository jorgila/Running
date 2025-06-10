package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.data.model.AnalyticModel
import com.estholon.running.domain.repository.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PreferencesResetUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke() : Boolean {
        preferencesRepository.resetPreferences()
        return true
    }

}