package com.estholon.running.domain.useCase.authentication

import com.estholon.running.data.manager.AnalyticsManager
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.data.model.AnalyticModel
import com.estholon.running.data.model.AuthRes
import com.estholon.running.domain.useCase.BaseSuspendUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val auth: AuthManager,
    private val analytics: AnalyticsManager
) : BaseSuspendUseCase<ResetPasswordUseCase.Params, Unit>(){

    data class Params (val email: String)

    override suspend fun execute(parameters: Params) : Unit {
        val result = auth.resetPassword(parameters.email)

        when(result){
            is AuthRes.Success -> {
                val analyticModel = AnalyticModel(
                    title = "Recover",
                    analyticsString = listOf(Pair("Email", "Successful password recovery"))
                )
                analytics.sendEvent(analyticModel)
            }
            is AuthRes.Error -> {
                val analyticModel = AnalyticModel(
                    title = "Recover",
                    analyticsString = listOf(
                        Pair(
                            "Email",
                            "Failed recovery: ${result.errorMessage}"
                        )
                    )
                )
                analytics.sendEvent(analyticModel)
                throw RuntimeException(result.errorMessage)
            }
        }
    }

}