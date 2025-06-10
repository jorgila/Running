package com.estholon.running.ui.screen.authentication

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.R
import com.estholon.running.domain.useCase.authentication.SignInEmailResultUseCase
import com.estholon.running.domain.useCase.authentication.SignUpEmailResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpEmailResultUseCase: SignUpEmailResultUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // VARIABLES

    private var _isLoading = MutableStateFlow<Boolean>(false)
    var isLoading : StateFlow<Boolean> = _isLoading

    // FUNCTIONS

    fun isEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun signUpEmail(
        email: String,
        password: String,
        navigateToSignIn: () -> Unit,
        communicateError: (String) -> Unit
    ) {
        _isLoading.value = true
        viewModelScope.launch {

            val signUp = signUpEmailResultUseCase(
                SignUpEmailResultUseCase.Params(email,password)
            )
            signUp.fold(
                onSuccess = { navigateToSignIn() },
                onFailure = { exception -> communicateError(exception.message ?: "Unknown error") }
            )

        }
        _isLoading.value = false
    }
}