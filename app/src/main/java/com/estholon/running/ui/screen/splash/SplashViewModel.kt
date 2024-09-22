package com.estholon.running.ui.screen.splash

import androidx.lifecycle.ViewModel
import com.estholon.running.data.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val auth: AuthManager
) : ViewModel() {

    fun isUserLogged() : Boolean {
        return auth.isUserLogged()
    }

}