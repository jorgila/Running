package com.estholon.running.domain.useCase.sharedPreferences

import com.estholon.running.data.manager.PreferencesManager
import javax.inject.Inject

class PreferencesPutLongUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend fun putLong(key: String, value: Long){
        preferencesManager.putLong(key,value)
    }

}