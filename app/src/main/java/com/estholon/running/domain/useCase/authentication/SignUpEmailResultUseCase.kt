package com.estholon.running.domain.useCase.authentication

import com.estholon.running.domain.model.AnalyticsModel
import com.estholon.running.domain.repository.AnalyticsRepository
import com.estholon.running.domain.repository.AuthenticationRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class SignUpEmailResultUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val analyticsRepository: AnalyticsRepository
) : BaseSuspendResultUseCase<SignUpEmailResultUseCase.Params,Unit>() {

    data class Params(
        val email: String,
        val password: String
    )

    override suspend fun execute(parameters: Params) {
        val result = authenticationRepository.signUpWithEmail(parameters.email,parameters.password)

        result.fold(
            onSuccess = {
                val analyticsModel = AnalyticsModel(
                    title = "Sign Up",
                    analyticsString = listOf(Pair("Email", "Successful Sign Up"))
                )
                analyticsRepository.sendEvent(analyticsModel)
            },
            onFailure = { exception ->
                val analyticsModel = AnalyticsModel(
                    title = "Sign Up", analyticsString = listOf(Pair("Email", "Failed Sign Up"))
                )
                analyticsRepository.sendEvent(analyticsModel)
                throw RuntimeException(exception.message)
            }
        )
    }
}