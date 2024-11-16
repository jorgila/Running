package com.estholon.running.ui.screen.home

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
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

    private val _stopped = MutableStateFlow<Boolean>(true)
    var stopped : StateFlow<Boolean> = _stopped

    private val _started = MutableStateFlow<Boolean>(false)
    var started : StateFlow<Boolean> = _started



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


    private var mpNotify : MediaPlayer? = null
    private var mpHard : MediaPlayer? = null
    private var mpSoft : MediaPlayer? = null

    private val _notificationVolume = MutableStateFlow<Float>(70.0f)
    val notificationVolume : StateFlow<Float> get() = _notificationVolume

    private val _runVolume = MutableStateFlow<Float>(70.0f)
    val runVolume : StateFlow<Float> get() = _runVolume

    private val _walkVolume = MutableStateFlow<Float>(70.0f)
    val walkVolume : StateFlow<Float> get() = _walkVolume

    private val _hardTrack = MutableStateFlow<Float>(0f)
    val hardTrack : StateFlow<Float> get() = _hardTrack

    private val _hardTrackPosition = MutableStateFlow<String>("00:00:00")
    val hardTrackPosition : StateFlow<String> get() = _hardTrackPosition

    private val _hardTrackRemaining = MutableStateFlow<String>("00:00:00")
    val hardTrackRemaining : StateFlow<String> get() = _hardTrackRemaining

    private val _softTrack = MutableStateFlow<Float>(0f)
    val softTrack : StateFlow<Float> get() = _softTrack

    private val _softTrackPosition = MutableStateFlow<String>("00:00:00")
    val softTrackPosition : StateFlow<String> get() = _softTrackPosition

    private val _softTrackRemaining = MutableStateFlow<String>("00:00:00")
    val softTrackRemaining : StateFlow<String> get() = _softTrackRemaining


    init {
        getUserInfo()
        setKPI()
        mHandler = Handler()
    }

    private fun setVolumes() {
        mpNotify?.setVolume(_notificationVolume.value/100.0f, _notificationVolume.value/100.0f)
        mpSoft?.setVolume(_walkVolume.value/100.0f, _walkVolume.value/100.0f)
        mpHard?.setVolume(_runVolume.value/100.0f, _runVolume.value/100.0f)
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
        val ms = (interval * 1000 * 60 * intervalDurationSeekbar).toLong()
        _runIntervalDuration.value = getFormattedStopWatchUseCase.getFormattedStopWatch(ms)
    }

    fun getWalkIntervalDuration(interval: Long, intervalDurationSeekbar: Float){
        val ms = (interval * 1000 * 60 * (1.0 - intervalDurationSeekbar)).toLong()
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

                if(mpHard!!.isPlaying){
                    _hardTrack.value = (mpHard!!.currentPosition.toFloat() / mpHard!!.duration.toFloat())*100
                }

                if(mpSoft!!.isPlaying){
                    _softTrack.value = (mpSoft!!.currentPosition.toFloat() / mpSoft!!.duration.toFloat())*100
                }

                updateTimesTrack(true,true)

                if(_intervalSwitch.value){
                    checkStopRun(timeInSeconds)
                    checkNewRun(timeInSeconds)
                } else {
                    mpHard?.start()
                }
                timeInSeconds += 1
                _chrono.value = getFormattedStopWatchUseCase.getFormattedStopWatch(timeInSeconds*1000)
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

    fun changeNotificationVolume(newPosition: Float) {
        _notificationVolume.value = newPosition
        setVolumes()
    }

    fun changeRunVolume(newPosition: Float) {
        _runVolume.value = newPosition
        setVolumes()
    }

    fun changeWalkVolume(newPosition: Float) {
        _walkVolume.value = newPosition
        setVolumes()
    }


    private fun checkStopRun(secs: Long){
        var seconds : Long = secs
        while(seconds > _intervalDuration.value) seconds -= _intervalDuration.value
        _timeRunning.value = getSecondsFromWatchUseCase.getSecondsFromWatch(_runIntervalDuration.value).toLong()



        if(seconds ==_timeRunning.value){
            _isWalkingInterval.value = true
            mpHard?.pause()
            mpNotify?.start()
            mpSoft?.start()
        } else {
            updateProgressBarRound(seconds)
        }
    }

    private fun checkNewRun(secs: Long){
        var seconds : Long = secs
        if(seconds.toInt() % _intervalDuration.value.toInt() == 0){
            if(secs > 0){
                _rounds.value++
                _isWalkingInterval.value = false
            }
            mpSoft?.start()
            mpSoft?.pause()
            mpNotify?.start()
            mpHard?.start()
        } else {
            updateProgressBarRound(seconds)
        }


    }

    private fun updateProgressBarRound(secs: Long) {

        var s = secs.toDouble()
        while(s>=_intervalDuration.value.toDouble()) s-=_intervalDuration.value

        if(_isWalkingInterval.value){
            var f = (s + 1 - _timeRunning.value.toDouble()) / (_intervalDuration.value.toDouble()-timeRunning.value.toDouble())
            _runningProgress.value = f.toFloat()
        } else {
            var f = (s + 1) / timeRunning.value.toDouble()
            _runningProgress.value = f.toFloat()
        }


    }

    fun changeStarted(b: Boolean) {
        _started.value = b
        if(_started.value){
            if(mpNotify==null){
                initMusic()
            }
        } else {
            mpHard?.stop()
            mpSoft?.stop()
            mpNotify?.stop()
            mpNotify = null
            mpHard = null
            mpSoft = null
        }
    }

    fun changeStopped(b: Boolean) {
        _stopped.value = b

        if(_stopped.value){
            if(_isWalkingInterval.value){
                mpSoft?.pause()
            } else {
                mpHard?.pause()
            }
        } else {
            if(_isWalkingInterval.value){
                mpSoft?.start()
            } else {
                mpHard?.start()
            }
        }


    }

    private fun initMusic() {
        mpNotify = MediaPlayer.create(context,R.raw.micmic)
        mpHard = MediaPlayer.create(context,R.raw.hardmusic)
        mpSoft = MediaPlayer.create(context,R.raw.softmusic)

        mpHard?.isLooping = true
        mpSoft?.isLooping = true

        setVolumes()
        updateTimesTrack(true,true)
    }

    private fun updateTimesTrack(timesH: Boolean, timesS: Boolean){
        if(timesH){
            _hardTrackPosition.value = getFormattedStopWatchUseCase.getFormattedStopWatch((mpHard!!.currentPosition).toLong())
            _hardTrackRemaining.value = getFormattedStopWatchUseCase.getFormattedStopWatch((mpHard!!.duration - mpHard!!.currentPosition).toLong())
        }
        if(timesS){
            _softTrackPosition.value = getFormattedStopWatchUseCase.getFormattedStopWatch((mpSoft!!.currentPosition).toLong())
            _softTrackRemaining.value = getFormattedStopWatchUseCase.getFormattedStopWatch((mpSoft!!.duration - mpSoft!!.currentPosition).toLong())
        }
    }

    fun changePositionHardTrack(newPosition: Float) {
        if (_started.value){
            if (!_isWalkingInterval.value){

                mpHard?.pause()
                mpHard?.seekTo(((mpHard!!.duration.toFloat())*newPosition/100).toInt())
                mpHard?.start()

                updateTimesTrack(true,false)
            }
        }
    }

    fun changePositionSoftTrack(newPosition: Float) {
        if(_started.value){
            if(_isWalkingInterval.value){
                mpSoft?.pause()
                mpSoft?.seekTo(((mpSoft!!.duration.toFloat())*newPosition/100).toInt())
                mpSoft?.start()

                updateTimesTrack(false,true)
            }
        }
    }

}