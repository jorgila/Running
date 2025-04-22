package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.data.manager.PreferencesManager
import javax.inject.Inject

class PreferencesGetLongUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend operator fun invoke(
        key: String,
        defaultValue: Long = 0
    ) : Long {
        return preferencesManager.getLong(key,defaultValue)
    }

}