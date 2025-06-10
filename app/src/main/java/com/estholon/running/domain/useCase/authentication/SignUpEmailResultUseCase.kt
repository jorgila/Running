package com.estholon.running.domain.useCase.authentication

import com.estholon.running.data.manager.AnalyticsManager
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.data.model.AnalyticModel
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class SignUpEmailResultUseCase @Inject constructor(
    private val auth: AuthManager,
    private val analytics: AnalyticsManager
) : BaseSuspendResultUseCase<SignUpEmailResultUseCase.Params,Unit>() {

    data class Params(
        val email: String,
        val password: String
    )

    override suspend fun execute(parameters: Params) {
        val result = auth.signUpWithEmail(parameters.email,parameters.password)

        result.fold(
            onSuccess = {
                val analyticModel = AnalyticModel(
                    title = "Sign Up",
                    analyticsString = listOf(Pair("Email", "Successful Sign Up"))
                )
                analytics.sendEvent(analyticModel)
            },
            onFailure = { exception ->
                val analyticModel = AnalyticModel(
                    title = "Sign Up", analyticsString = listOf(Pair("Email", "Failed Sign Up"))
                )
                analytics.sendEvent(analyticModel)
                throw RuntimeException(exception.message)
            }
        )
    }
}