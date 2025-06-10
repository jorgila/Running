package com.estholon.running.domain.useCase.others

import android.content.Context
import com.estholon.running.R
import com.estholon.running.domain.repository.AuthenticationRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCaseNoParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    @ApplicationContext private val context: Context
) : BaseSuspendResultUseCaseNoParams<String>() {

    override suspend fun execute(): String {
        return authenticationRepository.getCurrentEmail() ?: context.getString(R.string.anonimous)
    }
}