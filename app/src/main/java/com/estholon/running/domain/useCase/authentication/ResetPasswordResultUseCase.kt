package com.estholon.running.domain.useCase.authentication

import com.estholon.running.domain.model.AnalyticsModel
import com.estholon.running.domain.repository.AnalyticsRepository
import com.estholon.running.domain.repository.AuthenticationRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCase
import javax.inject.Inject

class ResetPasswordResultUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val analyticsRepository: AnalyticsRepository
) : BaseSuspendResultUseCase<ResetPasswordResultUseCase.Params, Unit>() {

    data class Params (val email: String)

    override suspend fun execute(parameters: Params) : Unit {
        val result = authenticationRepository.resetPassword(parameters.email)

        result.fold(
            onSuccess = {
                val analyticsModel = AnalyticsModel(
                    title = "Recover",
                    analyticsString = listOf(Pair("Email", "Successful password recovery"))
                )
                analyticsRepository.sendEvent(analyticsModel)
            },
            onFailure = { exception ->
                val analyticsModel = AnalyticsModel(
                    title = "Recover",
                    analyticsString = listOf(
                        Pair(
                            "Email",
                            "Failed recovery: ${exception.message}"
                        )
                    )
                )
                analyticsRepository.sendEvent(analyticsModel)
                throw RuntimeException(exception.message)
            }
        )

    }
}