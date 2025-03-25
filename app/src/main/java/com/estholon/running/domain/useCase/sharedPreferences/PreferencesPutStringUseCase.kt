package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.data.manager.PreferencesManager
import javax.inject.Inject

class PreferencesPutStringUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend fun putString(key: String, value: String){
        preferencesManager.putString(key,value)
    }

}