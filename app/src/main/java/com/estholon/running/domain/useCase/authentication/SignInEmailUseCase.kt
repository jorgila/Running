package com.estholon.running.domain.useCase.authentication

import com.estholon.running.data.manager.AnalyticsManager
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.data.model.AnalyticModel
import com.estholon.running.data.model.AuthRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInEmailUseCase @Inject constructor(
    private val auth: AuthManager,
    private val analytics: AnalyticsManager
){
    suspend operator fun invoke(
        email: String,
        password: String,
    ) : String {
        return withContext(Dispatchers.IO){
            var signIn = auth.signInWithEmail(email, password)

            when(val result = withContext(Dispatchers.IO){
                signIn
            }) {
                is AuthRes.Success -> {

                    val analyticModel = AnalyticModel(
                        title = "Sign In", analyticsString = listOf(Pair("Email", "Successful Sign In"))
                    )
                    analytics.sendEvent(analyticModel)
                    return@withContext "Success"
                }
                is AuthRes.Error -> {
                    val analyticModel = AnalyticModel(
                        title = "Sign In", analyticsString = listOf(Pair("Email", "Failed Sign In: ${result.errorMessage}"))
                    )
                    analytics.sendEvent(analyticModel)
                    signIn.let {
                        val string = it.toString().substringAfter("errorMessage=")
                        return@withContext string.substring(0,string.length - 1)
                    }
                }
            }
        }
    }

}