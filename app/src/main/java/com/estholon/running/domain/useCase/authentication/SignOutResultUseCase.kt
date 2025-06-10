package com.estholon.running.domain.useCase.authentication

import com.estholon.running.domain.repository.AuthenticationRepository
import com.estholon.running.domain.useCase.BaseSuspendResultUseCaseNoParams
import javax.inject.Inject

class SignOutResultUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
) : BaseSuspendResultUseCaseNoParams<Unit>(){

    override suspend fun execute() {
        authenticationRepository.signOut()
    }

}