package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.data.manager.PreferencesManager
import javax.inject.Inject

class PreferencesGetIntUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend fun getInt(
        key: String,
        defaultValue: Int = 0
    ) : Int {
        return preferencesManager.getInt(key,defaultValue)
    }

}