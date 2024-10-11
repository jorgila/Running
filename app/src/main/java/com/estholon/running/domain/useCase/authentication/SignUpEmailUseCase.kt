package com.estholon.running.domain.useCase.authentication

import com.estholon.running.data.manager.AnalyticsManager
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.data.model.AnalyticModel
import com.estholon.running.data.model.AuthRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignUpEmailUseCase @Inject constructor(
    private val auth: AuthManager,
    private val analytics: AnalyticsManager
) {
    suspend fun signUpEmail(
        email: String,
        password: String
    ): String {

        return withContext(Dispatchers.IO){
            val signUp = auth.signUpWithEmail(email,password)

            when (withContext(Dispatchers.IO) {
                signUp
            }) {
                is AuthRes.Success -> {

                    val analyticModel = AnalyticModel(
                        title = "Sign Up",
                        analyticsString = listOf(Pair("Email", "Successful Sign Up"))
                    )
                    analytics.sendEvent(analyticModel)
                    return@withContext "Success"
                }

                is AuthRes.Error -> {
                    val analyticModel = AnalyticModel(
                        title = "Sign Up", analyticsString = listOf(Pair("Email", "Failed Sign Up"))
                    )
                    analytics.sendEvent(analyticModel)
                    signUp.let {
                        val string = it.toString().substringAfter("errorMessage=")
                        return@withContext string.substring( 0 , string.length - 1 )
                    }
                }
            }
        }

    }


}