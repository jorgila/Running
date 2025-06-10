package com.estholon.running.domain.useCase.others

import android.content.Context
import com.estholon.running.R
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.domain.useCase.BaseSuspendResultUseCaseNoParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val auth: AuthManager,
    @ApplicationContext private val context: Context
) : BaseSuspendResultUseCaseNoParams<String>() {

    override suspend fun execute(): String {
        return auth.getCurrentEmail() ?: context.getString(R.string.anonimous)
    }
}