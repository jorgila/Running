package com.estholon.running.domain.useCase.authentication

import com.estholon.running.domain.model.AnalyticsModel
import com.estholon.running.domain.repository.AnalyticsRepository
import com.estholon.running.domain.repository.AuthenticationRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class SignInEmailResultUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val analyticsRepository: AnalyticsRepository
) : BaseSuspendResultUseCase<SignInEmailResultUseCase.SignInEmailParams,Unit>() {

    data class SignInEmailParams(
        val email: String,
        val password: String
    )

    override suspend fun execute(parameters: SignInEmailParams) {
        val result = authenticationRepository.signInWithEmail(parameters.email, parameters.password)

        result.fold(
            onSuccess = {
                val analyticsModel = AnalyticsModel(
                    title = "Sign In", analyticsString = listOf(Pair("Email", "Successful Sign In"))
                )
                analyticsRepository.sendEvent(analyticsModel)
            },
            onFailure = { exception ->
                val analyticsModel = AnalyticsModel(
                    title = "Sign In", analyticsString = listOf(Pair("Email", "Failed Sign In: ${exception.message}"))
                )
                analyticsRepository.sendEvent(analyticsModel)
                throw RuntimeException(exception.message)
            }
        )

    }
}