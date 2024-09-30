package com.estholon.running.ui.screen.authentication

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.R
import com.estholon.running.domain.useCase.authentication.SignInEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInEmailUseCase: SignInEmailUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // VARIABLES

    private var _isLoading = MutableStateFlow<Boolean>(false)
    var isLoading : StateFlow<Boolean> = _isLoading

    // FUNCTIONS

    fun isEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun signInEmail(
        email: String,
        password: String,
        navigateToHome: () -> Unit,
        communicateError: (String) -> Unit
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            val signIn = signInEmailUseCase.signInEmail(
                email,
                password
            )
            when(signIn){
                "Success" -> navigateToHome()
                null -> communicateError(context.getString(R.string.unknown_error))
                else -> communicateError(signIn)
            }
        }
        _isLoading.value = false
    }

}