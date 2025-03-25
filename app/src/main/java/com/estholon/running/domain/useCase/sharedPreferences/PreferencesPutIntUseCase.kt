package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.data.manager.PreferencesManager
import javax.inject.Inject

class PreferencesPutIntUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend fun putInt(key: String, value: Int){
        preferencesManager.putInt(key,value)
    }

}