package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.data.manager.PreferencesManager
import javax.inject.Inject

class PreferencesGetStringUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend fun getString(
        key: String,
        defaultValue: String = ""
    ) : String {
        return preferencesManager.getString(key,defaultValue)
    }

}