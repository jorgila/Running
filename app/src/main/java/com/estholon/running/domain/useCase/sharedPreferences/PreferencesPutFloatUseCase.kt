package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.data.manager.PreferencesManager
import javax.inject.Inject

class PreferencesPutFloatUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend fun putFloat(key: String, value: Float){
        preferencesManager.putFloat(key,value)
    }

}