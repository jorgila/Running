package com.estholon.running.domain.useCase.authentication

import com.estholon.running.data.manager.AnalyticsManager
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.domain.useCase.BaseSuspendUseCaseNoParams
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val auth: AuthManager,
    private val analytics: AnalyticsManager
) : BaseSuspendUseCaseNoParams<Unit>(){

    override suspend fun execute() {
        auth.signOut()
    }

}