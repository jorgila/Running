package com.estholon.running.ui.screen.authentication

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.domain.useCase.authentication.ResetPasswordUseCase
import com.estholon.running.domain.useCase.authentication.SignInEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecoverViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // VARIABLES

    private var _isLoading = MutableStateFlow<Boolean>(false)
    var isLoading : StateFlow<Boolean> = _isLoading

    // FUNCTIONS

    fun isEmail(email: String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun resetPassword(
        email: String,
        navigateToSignIn: () -> Unit,
        communicateError: (String) -> Unit
    ){
        _isLoading.value = true

        viewModelScope.launch {

            val reset = resetPasswordUseCase(email)

            when(reset){
                "Success" -> navigateToSignIn()
                else -> communicateError(reset)
            }
        }

        _isLoading.value = false
    }

}