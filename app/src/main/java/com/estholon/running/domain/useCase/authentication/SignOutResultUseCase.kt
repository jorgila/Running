package com.estholon.running.domain.useCase.authentication

import com.estholon.running.data.manager.AnalyticsManager
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.domain.useCase.BaseSuspendResultUseCaseNoParams
import javax.inject.Inject

class SignOutResultUseCase @Inject constructor(
    private val auth: AuthManager,
    private val analytics: AnalyticsManager
) : BaseSuspendResultUseCaseNoParams<Unit>(){

    override suspend fun execute() {
        auth.signOut()
    }

}