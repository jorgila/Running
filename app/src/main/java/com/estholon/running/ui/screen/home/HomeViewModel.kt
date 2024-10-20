package com.estholon.running.ui.screen.home

import android.content.Context
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

    private var _level = MutableStateFlow<String>(context.getString(R.string.level_0))
    var level : StateFlow<String> = _level

    private var _totalTime = MutableStateFlow<String>(context.getString(R.string.total_0))
    var totalTime : StateFlow<String> = _totalTime


    private var _currentKilometers = MutableStateFlow<Int>(0 )
    var currentKilometers : StateFlow<Int> = _currentKilometers

    private var _currentRuns = MutableStateFlow<Int>(0)
    var currentRuns : StateFlow<Int> = _currentRuns


    private var _totalKilometers = MutableStateFlow<Int>(0)
    var totalKilometers : StateFlow<Int> = _totalKilometers

    private var _totalRuns = MutableStateFlow<Int>(0)
    var totalRuns : StateFlow<Int> = _totalRuns

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