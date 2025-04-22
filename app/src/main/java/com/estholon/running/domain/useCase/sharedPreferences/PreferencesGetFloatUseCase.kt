package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.data.manager.PreferencesManager
import javax.inject.Inject

class PreferencesGetFloatUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend operator fun invoke(
        key: String,
        defaultValue: Float = 0F
    ) : Float {
        return preferencesManager.getFloat(key,defaultValue)
    }

}