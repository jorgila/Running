package com.estholon.running.ui.screen.home

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.R
import com.estholon.running.domain.useCase.authentication.SignOutUseCase
import com.estholon.running.domain.useCase.others.GetFormattedStopWatchUseCase
import com.estholon.running.domain.useCase.others.GetSecondsFromWatchUseCase
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
    private val getSecondsFromWatchUseCase: GetSecondsFromWatchUseCase,
    private val getFormattedStopWatchUseCase: GetFormattedStopWatchUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isLoading = MutableStateFlow<Boolean>(false)
    var isLoading : StateFlow<Boolean> = _isLoading

    private val _user = MutableStateFlow<String>(context.getString(R.string.anonimous))
    var user : StateFlow<String> = _user

    private val _level = MutableStateFlow<String>(context.getString(R.string.level_0))
    var level : StateFlow<String> = _level

    private val _totalTime = MutableStateFlow<String>(context.getString(R.string.total_0))
    var totalTime : StateFlow<String> = _totalTime

    private val _currentKilometers = MutableStateFlow<Double>(0.0 )
    var currentKilometers : StateFlow<Double> = _currentKilometers

    private val _currentAverageSpeed = MutableStateFlow<Double>(0.0 )
    var currentAverageSpeed : StateFlow<Double> = _currentAverageSpeed

    private val _currentSpeed = MutableStateFlow<Double>(0.0 )
    var currentSpeed : StateFlow<Double> = _currentSpeed

    private val _currentRuns = MutableStateFlow<Int>(0)
    var currentRuns : StateFlow<Int> = _currentRuns

    private val _recordKilometers = MutableStateFlow<Double>(0.0 )
    var recordKilometers : StateFlow<Double> = _recordKilometers

    private val _recordAverageSpeed = MutableStateFlow<Double>(0.0)
    var recordAverageSpeed : StateFlow<Double> = _recordAverageSpeed

    private val _recordSpeed = MutableStateFlow<Double>(0.0)
    var recordSpeed : StateFlow<Double> = _recordSpeed

    private val _goalKilometers = MutableStateFlow<Double>(0.0)
    var goalKilometers : StateFlow<Double> = _goalKilometers

    private val _totalKilometers = MutableStateFlow<Double>(0.0)
    var totalKilometers : StateFlow<Double> = _totalKilometers

    private val _totalRuns = MutableStateFlow<Int>(0)
    var totalRuns : StateFlow<Int> = _totalRuns

    private val _kilometersKPI = MutableStateFlow<Float>(0f)
    var kmKPI : StateFlow<Float> = _kilometersKPI

    private val _secondsFromWatch = MutableStateFlow<Int>(0)
    var secondsFromWatch : StateFlow<Int> = _secondsFromWatch

    private val _runIntervalDuration = MutableStateFlow("00:00")
    var runIntervalDuration : StateFlow<String> = _runIntervalDuration

    private val _walkIntervalDuration = MutableStateFlow("00:00")
    var walkIntervalDuration : StateFlow<String> = _walkIntervalDuration

    private val _chrono = MutableStateFlow<String>("00:00:00")
    var chrono : StateFlow<String> = _chrono

    private val _intervalSwitch = MutableStateFlow<Boolean>(false)
    val intervalSwitch : StateFlow<Boolean> get() = _intervalSwitch

    private val _intervalDuration = MutableStateFlow<Long>(1)
    val intervalDuration : StateFlow<Long> get() = _intervalDuration

    private val _timeRunning = MutableStateFlow<Long>(0)
    val timeRunning : StateFlow<Long> get() = _timeRunning


    private val _isWalkingInterval = MutableStateFlow<Boolean>(false)
    val isWalkingInterval : StateFlow<Boolean> get() = _isWalkingInterval

    private val _rounds = MutableStateFlow<Int>(1)
    val rounds : StateFlow<Int> get() = _rounds

    private val _runningProgress = MutableStateFlow<Float>(0f)
    var runningProgress : StateFlow<Float> = _runningProgress

    private var mHandler : Handler? = null
    private var mInterval = 1000
    private var timeInSeconds = 0L

    init {
        getUserInfo()
        setKPI()
        mHandler = Handler()
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

    fun getSecondsFromWatch(watch: String) {

        _secondsFromWatch.value = getSecondsFromWatchUseCase.getSecondsFromWatch(watch)
    }

    fun getRunIntervalDuration(interval: Long, intervalDurationSeekbar: Float){
        val ms = (interval * 1000 * 60 * intervalDurationSeekbar - 60 * 60 * 1000).toLong()
        _runIntervalDuration.value = getFormattedStopWatchUseCase.getFormattedStopWatch(ms)
    }

    fun getWalkIntervalDuration(interval: Long, intervalDurationSeekbar: Float){
        val ms = (interval * 1000 * 60 * (1.0 - intervalDurationSeekbar) - 60 * 60 * 1000).toLong()
        _walkIntervalDuration.value = getFormattedStopWatchUseCase.getFormattedStopWatch(ms)
    }

    fun setKPI(){
        _kilometersKPI.value =
           if(_recordKilometers.value<_goalKilometers.value){
            if(_currentKilometers.value < _goalKilometers.value) {
                (_currentKilometers.value / _goalKilometers.value).toFloat()
            } else {
                1f
            }
        } else {
            if(_currentKilometers.value < _recordKilometers.value) {
                (_currentKilometers.value / _recordKilometers.value).toFloat()
            } else {
                1f
            }
        }
    }


    var chronometer: Runnable = object : Runnable {
        override fun run() {
            try {

                if(_intervalSwitch.value){
                    checkStopRun(timeInSeconds)
                    checkNewRun(timeInSeconds)
                }


                timeInSeconds += 1
                _chrono.value = getFormattedStopWatchUseCase.getFormattedStopWatch(timeInSeconds * 1000 - 1000 * 60 * 60)
            } finally {
                mHandler!!.postDelayed(this,mInterval.toLong())
            }
        }
    }

    fun runChrono() {
        mHandler?.let{
            chronometer.run()
        }
    }

    fun stopChrono() {
        mHandler!!.removeCallbacks(chronometer)
    }

    fun resetChrono() {

        timeInSeconds = 0
        _rounds.value = 1
        _chrono.value = "00:00:00"
    }

    fun changeIntervalSwitch(){
        _intervalSwitch.value = !_intervalSwitch.value
    }

    fun changeIntervalDuration(minutes: Long) {
        _intervalDuration.value = minutes * 60
    }

    private fun checkStopRun(secs: Long){
        var seconds : Long = secs
        while(seconds > _intervalDuration.value) seconds -= _intervalDuration.value
        _timeRunning.value = getSecondsFromWatchUseCase.getSecondsFromWatch(_runIntervalDuration.value).toLong()

        if(seconds ==_timeRunning.value){
            _isWalkingInterval.value = true
        } else {
            updateProgressBarRound(seconds)
        }
    }

    private fun checkNewRun(secs: Long){
        var seconds : Long = secs
        if(seconds.toInt() % _intervalDuration.value.toInt() == 0 && secs > 0){
            _rounds.value++
            _isWalkingInterval.value = false
        } else {
            updateProgressBarRound(seconds)
        }
    }

    private fun updateProgressBarRound(secs: Long) {

        var s = secs.toDouble()
        while(s>=_intervalDuration.value.toDouble()) s-=_intervalDuration.value

        Log.i("HomeViewModel s",s.toString())
        if(_isWalkingInterval.value){
            var f = (s + 1 - _timeRunning.value.toDouble()) / (_intervalDuration.value.toDouble()-timeRunning.value.toDouble())
            _runningProgress.value = f.toFloat()
        } else {
            var f = (s + 1) / timeRunning.value.toDouble()
            _runningProgress.value = f.toFloat()
        }


    }

}