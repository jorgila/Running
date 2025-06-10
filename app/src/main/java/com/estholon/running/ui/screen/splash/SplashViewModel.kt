package com.estholon.running.ui.screen.splash

import androidx.lifecycle.ViewModel
import com.estholon.running.domain.repository.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    fun isUserLogged() : Boolean {
        return authenticationRepository.isUserLogged()
    }

}