package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.data.manager.PreferencesManager
import javax.inject.Inject

class PreferencesGetBooleanUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend operator fun invoke(
        key: String,
        defaultValue: Boolean = false
    ) : Boolean {
        return preferencesManager.getBoolean(key,defaultValue)
    }

}