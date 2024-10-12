package com.estholon.running.domain.useCase.authentication

import com.estholon.running.data.manager.AnalyticsManager
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.data.model.AnalyticModel
import com.estholon.running.data.model.AuthRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val auth: AuthManager,
    private val analytics: AnalyticsManager
) {

    suspend fun resetPassword(
        email: String
    ) : String {

        return withContext(Dispatchers.IO) {

            val recover = auth.resetPassword(email)

            when (val result = withContext(Dispatchers.IO) {
                recover
            }) {
                is AuthRes.Success -> {
                    val analyticModel = AnalyticModel(
                        title = "Recover",
                        analyticsString = listOf(Pair("Email", "Successful password recovery"))
                    )
                    analytics.sendEvent(analyticModel)
                    return@withContext "Success"

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
                    recover.let {
                        val string = it.toString().substringAfter("errorMessage=")
                        return@withContext string.substring(0, string.length - 1)
                    }
                }
            }
        }

    }
}