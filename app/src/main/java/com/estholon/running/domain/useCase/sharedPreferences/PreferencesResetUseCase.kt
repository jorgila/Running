package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.data.manager.PreferencesManager
import com.estholon.running.data.model.AnalyticModel
import com.estholon.running.data.model.AuthRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PreferencesResetUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend operator fun invoke() : Boolean {
        preferencesManager.resetPreferences()
        return true
    }

}