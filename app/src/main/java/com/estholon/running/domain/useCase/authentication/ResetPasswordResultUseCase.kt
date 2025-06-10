package com.estholon.running.domain.useCase.authentication

import com.estholon.running.data.manager.AnalyticsManager
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.data.model.AnalyticModel
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class ResetPasswordResultUseCase @Inject constructor(
    private val auth: AuthManager,
    private val analytics: AnalyticsManager
) : BaseSuspendResultUseCase<ResetPasswordResultUseCase.Params, Unit>() {

    data class Params (val email: String)

    override suspend fun execute(parameters: Params) : Unit {
        val result = auth.resetPassword(parameters.email)

        result.fold(
            onSuccess = {
                val analyticModel = AnalyticModel(
                    title = "Recover",
                    analyticsString = listOf(Pair("Email", "Successful password recovery"))
                )
                analytics.sendEvent(analyticModel)
            },
            onFailure = { exception ->
                val analyticModel = AnalyticModel(
                    title = "Recover",
                    analyticsString = listOf(
                        Pair(
                            "Email",
                            "Failed recovery: ${exception.message}"
                        )
                    )
                )
                analytics.sendEvent(analyticModel)
                throw RuntimeException(exception.message)
            }
        )

    }
}