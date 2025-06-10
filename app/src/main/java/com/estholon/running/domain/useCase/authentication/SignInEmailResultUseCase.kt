package com.estholon.running.domain.useCase.authentication

import com.estholon.running.data.manager.AnalyticsManager
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.data.model.AnalyticModel
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class SignInEmailResultUseCase @Inject constructor(
    private val auth: AuthManager,
    private val analytics: AnalyticsManager
) : BaseSuspendResultUseCase<SignInEmailResultUseCase.Params,Unit>() {

    data class Params(
        val email: String,
        val password: String
    )

    override suspend fun execute(parameters: Params) {
        val result = auth.signInWithEmail(parameters.email, parameters.password)

        result.fold(
            onSuccess = {
                val analyticModel = AnalyticModel(
                    title = "Sign In", analyticsString = listOf(Pair("Email", "Successful Sign In"))
                )
                analytics.sendEvent(analyticModel)
            },
            onFailure = { exception ->
                val analyticModel = AnalyticModel(
                    title = "Sign In", analyticsString = listOf(Pair("Email", "Failed Sign In: ${exception.message}"))
                )
                analytics.sendEvent(analyticModel)
                throw RuntimeException(exception.message)
            }
        )

    }
}