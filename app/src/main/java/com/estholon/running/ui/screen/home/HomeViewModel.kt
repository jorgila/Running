package com.estholon.running.ui.screen.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.R
import com.estholon.running.domain.useCase.authentication.SignOutUseCase
import com.estholon.running.domain.useCase.others.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var _isLoading = MutableStateFlow<Boolean>(false)
    var isLoading : StateFlow<Boolean> = _isLoading

    private var _user = MutableStateFlow<String>(context.getString(R.string.anonimous))
    var user : StateFlow<String> = _user

    init {
        getUserInfo()
    }

    fun logout() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _isLoading.value = true
                signOutUseCase.signOut()
                _isLoading.value = false
            }
        }
    }

    fun getUserInfo(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _user.value = getUserInfoUseCase.getUserInfo()
            }
        }
    }

}