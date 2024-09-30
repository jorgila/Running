package com.estholon.running.ui.screen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.domain.useCase.authentication.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private var _isLoading = MutableStateFlow<Boolean>(false)
    var isLoading : StateFlow<Boolean> = _isLoading

    fun logout() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _isLoading.value = true
                signOutUseCase.signOut()
                _isLoading.value = false
            }
        }
    }
}