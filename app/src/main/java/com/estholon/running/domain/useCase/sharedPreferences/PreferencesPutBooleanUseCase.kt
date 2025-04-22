package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.data.manager.PreferencesManager
import javax.inject.Inject

class PreferencesPutBooleanUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend operator fun invoke(
        key: String,
        value: Boolean
    ){
        preferencesManager.putBoolean(key,value)
    }

}