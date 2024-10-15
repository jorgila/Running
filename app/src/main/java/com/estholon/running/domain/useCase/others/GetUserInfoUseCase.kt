package com.estholon.running.domain.useCase.others

import android.content.Context
import com.estholon.running.R
import com.estholon.running.data.manager.AuthManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val auth: AuthManager,
    @ApplicationContext private val context: Context
) {
    suspend fun getUserInfo(): String {
        return withContext(Dispatchers.IO){
            auth.getCurrentEmail() ?: context.getString(R.string.anonimous)
        }
    }
}