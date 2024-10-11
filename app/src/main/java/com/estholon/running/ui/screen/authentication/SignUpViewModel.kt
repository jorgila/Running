package com.estholon.running.ui.screen.authentication

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.R
import com.estholon.running.domain.useCase.authentication.SignInEmailUseCase
import com.estholon.running.domain.useCase.authentication.SignUpEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.sign

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpEmailUseCase: SignUpEmailUseCase,
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
            val signUp = signUpEmailUseCase.signUpEmail(
                email,
                password
            )
            when(signUp){
                "Success" -> navigateToSignIn()
                null -> communicateError(context.getString(R.string.unknown_error))
                else -> communicateError(signUp)
            }
        }
        _isLoading.value = false
    }
}