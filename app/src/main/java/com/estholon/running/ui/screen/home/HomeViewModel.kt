package com.estholon.running.ui.screen.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.R
import com.estholon.running.common.Constants.INTERVAL_LOCATION
import com.estholon.running.common.Constants.LIMIT_DISTANCE_ACCEPTED
import com.estholon.running.common.SharedPreferencesKeys
import com.estholon.running.domain.model.AudioModel
import com.estholon.running.domain.model.LocationModel
import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.model.TotalModel
import com.estholon.running.domain.repository.AudioRepository
import com.estholon.running.domain.useCase.audio.GetProgressUseCase
import com.estholon.running.domain.useCase.audio.HandleRunningIntervalUseCase
import com.estholon.running.domain.useCase.audio.HandleRunningStateChangeUseCase
import com.estholon.running.domain.useCase.audio.InitializeAudioUseCase
import com.estholon.running.domain.useCase.audio.PauseAudioUseCase
import com.estholon.running.domain.useCase.audio.PlayAudioUseCase
import com.estholon.running.domain.useCase.audio.ReleaseAudioUseCase
import com.estholon.running.domain.useCase.audio.SeekAudioUseCase
import com.estholon.running.domain.useCase.audio.SetAudioVolumeUseCase
import com.estholon.running.domain.useCase.audio.StopAllAudioUseCase
import com.estholon.running.domain.useCase.audio.StopAudioUseCase
import com.estholon.running.domain.useCase.audio.UpdateAllVolumesUseCase
import com.estholon.running.domain.useCase.authentication.SignOutResultUseCase
import com.estholon.running.domain.useCase.firestore.GetLevelsResultUseCase
import com.estholon.running.domain.useCase.firestore.GetTotalsResultUseCase
import com.estholon.running.domain.useCase.firestore.SetLocationSuspendResultUseCase
import com.estholon.running.domain.useCase.firestore.SetRunSuspendResultUseCase
import com.estholon.running.domain.useCase.firestore.SetTotalsSuspendResultUseCase
import com.estholon.running.domain.useCase.others.GetFormattedStopWatchUseCase
import com.estholon.running.domain.useCase.others.GetSecondsFromWatchUseCase
import com.estholon.running.domain.useCase.others.GetStringWithDHMSFromMilisecondsUseCase
import com.estholon.running.domain.useCase.others.GetUserInfoUseCase
import com.estholon.running.domain.useCase.others.RoundNumberUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesGetBooleanUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesGetFloatUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesGetIntUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesPutBooleanUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesPutFloatUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesPutIntUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesResetUseCase
import com.estholon.running.ui.screen.finished.FinishedScreenViewState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.MapType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class HomeViewModel @Inject constructor(

    // Authentication Use Cases
    private val signOutUseCase: SignOutResultUseCase,

    // Others Use Cases
    private val getFormattedStopWatchUseCase: GetFormattedStopWatchUseCase,
    private val getSecondsFromWatchUseCase: GetSecondsFromWatchUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val roundNumberUseCase: RoundNumberUseCase,
    private val getStringWithDHMSFromMilisecondsUseCase: GetStringWithDHMSFromMilisecondsUseCase,

    // Firestore Use Cases
    private val getTotalsUseCase: GetTotalsResultUseCase,
    private val getLevelsUseCase: GetLevelsResultUseCase,
    private val setTotalsUseCase: SetTotalsSuspendResultUseCase,
    private val setRunUseCase: SetRunSuspendResultUseCase,
    private val setLocationUseCase: SetLocationSuspendResultUseCase,

    // Preferences Use Cases
    private val preferencesGetBooleanUseCase: PreferencesGetBooleanUseCase,
    private val preferencesPutBooleanUseCase: PreferencesPutBooleanUseCase,
    private val preferencesGetFloatUseCase: PreferencesGetFloatUseCase,
    private val preferencesPutFloatUseCase: PreferencesPutFloatUseCase,
    private val preferencesGetIntUseCase: PreferencesGetIntUseCase,
    private val preferencesPutIntUseCase: PreferencesPutIntUseCase,
    private val preferencesResetUseCase: PreferencesResetUseCase,

    // Audio Use Cases
    private val initializeAudioUseCase: InitializeAudioUseCase,
    private val playAudioUseCase: PlayAudioUseCase,
    private val pauseAudioUseCase: PauseAudioUseCase,
    private val stopAudioUseCase: StopAudioUseCase,
    private val stopAllAudioUseCase: StopAllAudioUseCase,
    private val setAudioVolumeUseCase: SetAudioVolumeUseCase,
    private val seekAudioUseCase: SeekAudioUseCase,
    private val getAudioProgressUseCase: GetProgressUseCase,
    private val releaseAudioUseCase: ReleaseAudioUseCase,
    private val handleRunningIntervalUseCase: HandleRunningIntervalUseCase,
    private val updateAllAudioVolumesUseCase: UpdateAllVolumesUseCase,
    private val handleRunningStateChangeUseCase: HandleRunningStateChangeUseCase,

    // Context
    @ApplicationContext private val context: Context

) : ViewModel() {

    companion object {
        val REQUIRED_PERMISSIONS_GPS =
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
    }

    // UI STATE

    private val _homeUIState = MutableStateFlow<HomeScreenViewState.HomeUIState>(HomeScreenViewState.HomeUIState())
    val homeUIState : StateFlow<HomeScreenViewState.HomeUIState> = _homeUIState

    private val _finishedUIState = MutableStateFlow<FinishedScreenViewState.FinishedUIState>(FinishedScreenViewState.FinishedUIState())
    val finishedUIState : StateFlow<FinishedScreenViewState.FinishedUIState> = _finishedUIState

    // VARIABLES

    private val _stopped = MutableStateFlow<Boolean>(true)
    var stopped : StateFlow<Boolean> = _stopped

    private val _started = MutableStateFlow<Boolean>(false)
    var started : StateFlow<Boolean> = _started

    private val _isLoading = MutableStateFlow<Boolean>(false)
    var isLoading : StateFlow<Boolean> = _isLoading

    private val _timeRunning = MutableStateFlow<Long>(0)
    val timeRunning : StateFlow<Long> get() = _timeRunning

    private val _isWalkingInterval = MutableStateFlow<Boolean>(false)
    val isWalkingInterval : StateFlow<Boolean> get() = _isWalkingInterval

    private val _runningProgress = MutableStateFlow<Float>(0f)
    var runningProgress : StateFlow<Float> = _runningProgress

    private var mHandler : Handler? = null
    private var mInterval = 1000
    private var timeInSeconds = 0L

    private val _locationStatus = MutableStateFlow<Boolean>(false)
    val locationStatus : StateFlow<Boolean> get() = _locationStatus

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val _totalTime = MutableStateFlow<Double>(0.00)
    val totalTime : StateFlow<Double> get() = _totalTime

    // AUDIO
    private var audioProgressJob: Job? = null
    private var isAudioInitialized = false

    // OTROS
    private val PERMISSION_ID = 42
    private var flagSavedLocation = false
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var minLatitude: Double? = null
    private var maxLatitude: Double? = null
    private var minLongitude: Double? = null
    private var maxLongitude: Double? = null

    private var init_lt: Double = 0.0
    private var init_ln: Double = 0.0

    private var distance: Double = 0.0

    private val _maxSpeed = MutableStateFlow<Double>(0.0)
    val maxSpeed : StateFlow<Double> get() = _maxSpeed

    private var avgSpeed: Double = 0.0
    private var speed: Double = 0.0

    // DATE
    private lateinit var startDate : String
    private lateinit var startTime : String

    // GOOGLE MAP

    private val _coordinates = MutableStateFlow(emptyList<LatLng>())
    val coordinates: StateFlow<List<LatLng>> = _coordinates

    // UI

    //// INTERVAL SETTINGS

    private val _intervalDuration = MutableStateFlow<Long>(1)
    val intervalDuration : StateFlow<Long> get() = _intervalDuration

    init {

        getUserInfo()
        setKPI()
        mHandler = Handler()
        initPermissionGPS()
        initPreferences()
        initTotals()
        initLevels()
        initAudio()
    }

    fun initAudio(){
        Log.d("HomeViewModel", "initAudio() called")
        viewModelScope.launch {
            Log.d("HomeViewModel", "About to call initializeAudioUseCase")
            initializeAudioUseCase().fold(
                onSuccess = {
                    Log.d("HomeViewModel", "✅ Audio initialized successfully")
                    isAudioInitialized = true

                    Log.d("HomeViewModel", "About to update audio volumes...")
                    updateAudioVolumes()

                    Log.d("HomeViewModel", "About to start audio progress tracking...")
                    startAudioProgressTracking()

                    Log.d("HomeViewModel", "Audio initialization complete")
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel", "❌ Failed to initialize audio", exception)
                    isAudioInitialized = false
                }
            )
        }
    }

    fun initPreferences(){

        viewModelScope.launch {

            _homeUIState.update { homeUIState ->
                homeUIState.copy(
                    goalSwitch = preferencesGetBooleanUseCase(SharedPreferencesKeys.SP_GOAL_SWITCH,false),
                    goalDurationSelected = preferencesGetBooleanUseCase(SharedPreferencesKeys.SP_DURATION_SELECTED,true),
                    goalHoursDefault = preferencesGetIntUseCase(SharedPreferencesKeys.SP_HOURS_GOAL_DEFAULT,0),
                    goalMinutesDefault = preferencesGetIntUseCase(SharedPreferencesKeys.SP_MINUTES_GOAL_DEFAULT,0),
                    goalSecondsDefault = preferencesGetIntUseCase(SharedPreferencesKeys.SP_SECONDS_GOAL_DEFAULT,0),
                    goalDistanceDefault = preferencesGetIntUseCase(SharedPreferencesKeys.SP_KILOMETERS_GOAL_DEFAULT,0),
                    goalNotifyCheck = preferencesGetBooleanUseCase(SharedPreferencesKeys.SP_NOTIFY_GOAL,false),
                    goalAutomaticFinishCheck = preferencesGetBooleanUseCase(SharedPreferencesKeys.SP_AUTOMATIC_FINISH, false),
                    intervalSwitch = preferencesGetBooleanUseCase(SharedPreferencesKeys.SP_INTERVAL_SWITCH, false),
                    intervalDefault = preferencesGetIntUseCase(SharedPreferencesKeys.SP_INTERVAL_DEFAULT, 5),
                    intervalDurationSeekbar = preferencesGetFloatUseCase(SharedPreferencesKeys.SP_INTERVAL_DURATION_SEEKBAR, 0.5F),
                    audioSwitch = preferencesGetBooleanUseCase(SharedPreferencesKeys.SP_AUDIO_SWITCH, false),
                    audioRunVolume = preferencesGetFloatUseCase(SharedPreferencesKeys.SP_RUN_VOLUME, 70.0F),
                    audioWalkVolume = preferencesGetFloatUseCase(SharedPreferencesKeys.SP_WALK_VOLUME, 70.0F),
                    audioNotificationVolume = preferencesGetFloatUseCase(SharedPreferencesKeys.SP_NOTIFICATION_VOLUME, 70.0F),
                )
            }

        }

    }

    fun initTotals(){

        viewModelScope.launch {
            getTotalsUseCase.invoke().collect{ result ->

                result.fold(
                    onSuccess = { totals ->

                        _totalTime.value = totals.totalTime

                        _homeUIState.update { homeUIState ->
                            homeUIState.copy(
                                kpiRecordAvgSpeed = totals.recordAvgSpeed,
                                kpiRecordDistance = totals.recordDistance,
                                kpiRecordSpeed = totals.recordSpeed,
                                kpiTotalDistance = totals.totalDistance,
                                kpiTotalRuns = totals.totalRuns,
                                kpiTotalTime = getStringWithDHMSFromMilisecondsUseCase(totals.totalTime)
                            )
                        }

                    },
                    onFailure = { exception ->
                        Log.e("HomeViewModel","Error loading totals", exception)
                    }
                )
            }
        }
    }

    fun initLevels(){
        viewModelScope.launch {
            getLevelsUseCase.invoke().collect{ result ->
                result.fold(
                    onSuccess = { levels ->
                        for (level in levels) {

                            if (_homeUIState.value.kpiTotalDistance < level.distanceTarget || _homeUIState.value.kpiTotalRuns < level.runsTarget) {
                                _homeUIState.update { homeUIState ->
                                    homeUIState.copy(
                                        kpiLevel = level.level,
                                        kpiLevelDistance = level.distanceTarget,
                                        kpiLevelRuns = level.runsTarget
                                    )
                                }
                                break
                            }
                        }
                    },
                    onFailure = { exception ->
                        Log.e("HomeViewModel","Error loading levels", exception)
                    }
                )
            }
        }
    }

    fun initPermissionGPS() {
        
        if(allPermissionsGrantedGPS())
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        else
            requestPermissionLocation()
    }

    private fun requestPermissionLocation() {

    }

    private fun allPermissionsGrantedGPS() = REQUIRED_PERMISSIONS_GPS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun updateAudioVolumes() {
        if (!isAudioInitialized) return

        viewModelScope.launch {
            updateAllAudioVolumesUseCase(
                UpdateAllVolumesUseCase.VolumeParams(
                    runVolume = _homeUIState.value.audioRunVolume,
                    walkVolume = _homeUIState.value.audioWalkVolume,
                    notificationVolume = _homeUIState.value.audioNotificationVolume
                )
            ).fold(
                onSuccess = {
                    Log.d("HomeViewModel", "Audio volumes updated successfully")
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel", "Failed to update audio volumes", exception)
                }
            )
        }
    }

    private fun startAudioProgressTracking() {
        audioProgressJob?.cancel()
        audioProgressJob = viewModelScope.launch {
            while (isAudioInitialized && isActive) {
                getAudioProgressUseCase(GetProgressUseCase.GetProgressParams(AudioModel.RUN))
                    .getOrNull()?.let { progress ->
                        _homeUIState.update { currentState ->
                            currentState.copy(
                                audioRunTrack = progress.progressPercentage,
                                audioRunTrackPosition = getFormattedStopWatchUseCase(progress.currentPosition),
                                audioRunRemainingTrackPosition = getFormattedStopWatchUseCase(progress.duration - progress.currentPosition)
                            )
                        }

                        _homeUIState.update { homeUIState ->
                            homeUIState.copy(
                                audioRunTrack = progress.progressPercentage,
                                audioRunTrackPosition = getFormattedStopWatchUseCase(progress.currentPosition),
                                audioRunRemainingTrackPosition = getFormattedStopWatchUseCase(progress.duration - progress.currentPosition)
                            )
                        }
                    }

                getAudioProgressUseCase(GetProgressUseCase.GetProgressParams(AudioModel.WALK))
                    .getOrNull()?.let { progress ->
                        _homeUIState.update { currentState ->
                            currentState.copy(
                                audioWalkTrack = progress.progressPercentage,
                                audioWalkTrackPosition = getFormattedStopWatchUseCase(progress.currentPosition),
                                audioWalkRemainingTrackPosition = getFormattedStopWatchUseCase(progress.duration - progress.currentPosition)
                            )
                        }

                        _homeUIState.update { homeUIState ->
                            homeUIState.copy(
                                audioWalkTrack = progress.progressPercentage,
                                audioWalkTrackPosition = getFormattedStopWatchUseCase(progress.currentPosition),
                                audioWalkRemainingTrackPosition = getFormattedStopWatchUseCase(progress.duration - progress.currentPosition)
                            )
                        }
                    }

            }
            delay(1000)
        }
    }

    private fun handleIntervalChange(isWalkingInterval: Boolean) {
        viewModelScope.launch {
            handleRunningIntervalUseCase(
                HandleRunningIntervalUseCase.IntervalParams(
                    isWalkingInterval = isWalkingInterval,
                )
            ).fold(
                onSuccess = {
                    _isWalkingInterval.value = isWalkingInterval
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel", "Failed to handle interval change", exception)
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _isLoading.value = true
                signOutUseCase()
                _isLoading.value = false
            }
        }
    }

    fun getUserInfo(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val user = getUserInfoUseCase()

                user.fold(
                    onSuccess = { userString ->
                        _homeUIState.update { homeUIState ->
                            homeUIState.copy(
                                user = userString
                            )
                        }
                    },
                    onFailure = {
                        _homeUIState.update { homeUIState ->
                            homeUIState.copy(
                                user = "Usuario desconocido"
                            )
                        }
                    }
                )
            }
        }
    }

    fun getIntervalRunDuration(interval: Long, seekbar: Float){

        val ms = ((interval * 60 * seekbar / 15.0).roundToInt() * 15 * 1000).toLong()

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                intervalRunDuration = getFormattedStopWatchUseCase(ms)
            )
        }

    }

    fun getIntervalWalkDuration(interval: Long, seekbar: Float){

        val ms = ((interval * 60 * (1.0 - seekbar) / 15.0).roundToInt() * 15 * 1000).toLong()

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                intervalWalkDuration = getFormattedStopWatchUseCase(ms)
            )
        }

    }

    fun setKPI(){

        val kpiDistanceFloat = if(_homeUIState.value.kpiRecordDistance<_homeUIState.value.goalDistance){
            if(_homeUIState.value.kpiDistance < _homeUIState.value.goalDistance) {
                (_homeUIState.value.kpiDistance / _homeUIState.value.goalDistance).toFloat()
            } else {
                1f
            }
        } else {
            if(_homeUIState.value.kpiDistance < _homeUIState.value.kpiRecordDistance) {
                (_homeUIState.value.kpiDistance / _homeUIState.value.kpiRecordDistance).toFloat()
            } else {
                _homeUIState.update { homeUIState ->
                    homeUIState.copy(
                        kpiRecordDistance = _homeUIState.value.kpiDistance
                    )
                }
                1f
            }
        }

        val kpiAvgSpeedFloat =
            if(_homeUIState.value.kpiAvgSpeed < _homeUIState.value.kpiRecordAvgSpeed) {
                (_homeUIState.value.kpiAvgSpeed / _homeUIState.value.kpiRecordAvgSpeed).toFloat()
            } else {
                _homeUIState.update { homeUIState ->
                    homeUIState.copy(
                        kpiRecordAvgSpeed = _homeUIState.value.kpiAvgSpeed
                    )
                }
                1f
            }

        val kpiSpeedFloat =
            if(_homeUIState.value.kpiSpeed < _homeUIState.value.kpiRecordSpeed) {
                (_homeUIState.value.kpiSpeed / _homeUIState.value.kpiRecordSpeed).toFloat()
            } else {
                _homeUIState.update { homeUIState ->
                    homeUIState.copy(
                        kpiRecordSpeed = _homeUIState.value.kpiSpeed
                    )
                }
                1f
            }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                kpiDistanceCircularSeekbarValue = kpiDistanceFloat,
                kpiAvgSpeedCircularSeekbarValue = kpiAvgSpeedFloat,
                kpiSpeedCircularSeekbarValue = kpiSpeedFloat,
            )
        }
    }

    var chronometer: Runnable = object : Runnable {
        override fun run() {
            try {

                if(timeInSeconds==0L){

                    startDate = SimpleDateFormat("yyyy/MM/dd").format(Date())
                    startTime = SimpleDateFormat("HH:mm:ss").format(Date())

                    _homeUIState.update { homeUIState ->
                        homeUIState.copy(
                            runId = "${startDate.replace("/","")}${startTime.replace(":","")}"
                        )
                    }

                    flagSavedLocation = false
                    manageLocation()
                    flagSavedLocation = true
                    manageLocation()
                }

                if(
                    _locationStatus.value
                    && timeInSeconds.toInt() % INTERVAL_LOCATION == 0
                ) manageLocation()

                if( homeUIState.value.intervalSwitch ){
                    Log.v("HomeViewModel", "Interval mode is ON")
                    checkStopRun(timeInSeconds)
                    checkNewRun(timeInSeconds)
                } else {
                    Log.v("HomeViewModel", "Interval mode is OFF")
                    if (isAudioInitialized && !_stopped.value) {
                        Log.v("HomeViewModel", "Ensuring RUN music is playing...")
                        viewModelScope.launch {
                            playAudioUseCase(PlayAudioUseCase.PlayAudioParams(AudioModel.RUN)).fold(
                                onSuccess = {
                                    Log.v("HomeViewModel", "✅ RUN music playing")
                                },
                                onFailure = { exception ->
                                    Log.e("HomeViewModel", "❌ Failed to play RUN music", exception)
                                }
                            )
                        }
                    } else {
                        Log.v("HomeViewModel", "Not playing music - isAudioInitialized: $isAudioInitialized, stopped: ${_stopped.value}")
                    }
                }
                timeInSeconds += 1

                _homeUIState.update { homeUIState ->
                    homeUIState.copy(
                        kpiDuration = getFormattedStopWatchUseCase(timeInSeconds*1000)
                    )
                }

                setKPI()

            } finally {
                mHandler!!.postDelayed(this,mInterval.toLong())
            }
        }
    }

    private fun manageLocation() {
        if(checkPermission()){
            if(CheckLocationServices(context)){
                if(
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ){
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        requestNewLocationData()
                    }
                } //else activateLocation()
            }
        } else requestPermissionLocation()
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData(){

        var mLocationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY,0)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(0)
            .setMaxUpdateDelayMillis(0)
            .setMaxUpdates(1)
            .build()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper())

    }

    private val mLocationCallback = object: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let {
                var mLastLocation : Location = locationResult.lastLocation!!
                init_lt = mLastLocation.latitude
                init_ln = mLastLocation.longitude

                if(timeInSeconds > 0) registerNewLocation(mLastLocation)
            }
        }
    }

    private fun registerNewLocation(location: Location) {
        var new_latitude: Double = location.latitude
        var new_longitude: Double = location.longitude

        // if(latitude != new_latitude || longitude != new_longitude){
            if(flagSavedLocation){
                if (timeInSeconds >= INTERVAL_LOCATION){
                    var distanceInterval = calculateDistance(new_latitude,new_longitude)
                    if(distanceInterval <= LIMIT_DISTANCE_ACCEPTED) {
                        updateSpeeds(distanceInterval)
                        refreshInterfaceData()
                        setLocation(location)
                        _coordinates.update {
                            it + LatLng(new_latitude,new_longitude)
                        }
                        _homeUIState.update { homeUIState ->
                            homeUIState.copy(
                                mapLatLongTarget = LatLng(new_latitude,new_longitude)
                            )
                        }
                    }
                }
            }
        // }

        latitude = new_latitude
        longitude = new_longitude

        if(minLatitude == null){
            minLatitude = latitude
            maxLatitude = latitude
            minLongitude = longitude
            maxLongitude = longitude
        }

        if(latitude < minLatitude!!) minLatitude = latitude
        if(latitude > maxLatitude!!) maxLatitude = latitude
        if(longitude < minLongitude!!) minLongitude = longitude
        if(longitude > maxLongitude!!) maxLongitude = longitude

        if(location.hasAltitude()){
            if(_homeUIState.value.kpiMaxAltitude == null){
                _homeUIState.update { homeUIState ->
                    homeUIState.copy(
                        kpiMinAltitude = location.altitude,
                        kpiMaxAltitude = location.altitude
                    )
                }
            }
            if(location.altitude > _homeUIState.value.kpiMaxAltitude!!) {
                _homeUIState.update { homeUIState ->
                    homeUIState.copy(
                        kpiMaxAltitude = location.altitude
                    )
                }
            }
            if(location.altitude < _homeUIState.value.kpiMinAltitude!!) {
                _homeUIState.update { homeUIState ->
                    homeUIState.copy(
                        kpiMinAltitude = location.altitude
                    )
                }
            }

        }
    }

    private fun setLocation(location: Location){

        var id = homeUIState.value.runId ?: ""

        var docName = timeInSeconds.toString()
        while(docName.length < 5) docName = "0" + docName

        val ms = speed == _maxSpeed.value && speed > 0

        viewModelScope.launch {
            setLocationUseCase(
                SetLocationSuspendResultUseCase.SetLocationParams(
                    id,
                    docName,
                    LocationModel(
                        _homeUIState.value.kpiDuration,
                        location.latitude,
                        location.longitude,
                        location.altitude,
                        location.hasAltitude(),
                        location.speed,
                        _homeUIState.value.kpiSpeed,
                        ms,
                        _isWalkingInterval.value
                    )
                )
            )
        }
    }

    private fun refreshInterfaceData() {

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                kpiDistance = Math.round(distance * 100).toDouble()/100,
                kpiAvgSpeed = Math.round(avgSpeed * 10).toDouble()/10,
                kpiSpeed = Math.round(speed * 10).toDouble()/10
            )
        }

    }

    private fun updateSpeeds(d: Double) {

        // Se pasa la distancia a metros para calcular los m/s y se convierte a km/h

        speed = ((d * 1000) / INTERVAL_LOCATION) * 3.6
        if(speed > _maxSpeed.value) _maxSpeed.value = (speed*100).roundToInt()/100.0
        avgSpeed = ((distance * 1000) / timeInSeconds) * 3.6
    }

    private fun calculateDistance(n_lt: Double, n_ln: Double): Double {
        val radioTierra = 6371.0 // en Kilometros

        val dLat = Math.toRadians(n_lt - latitude)
        val dLng = Math.toRadians(n_ln - longitude)
        val sindLat = Math.sin(dLat/2)
        val sindLng = Math.sin(dLng/2)
        val va1 =
            Math.pow(sindLat, 2.0) + (
                Math.pow(sindLng, 2.0)
                * Math.cos(Math.toRadians(latitude))
                * Math.cos(Math.toRadians(n_lt))
            )
        val va2 = 2 * Math.atan2(
            Math.sqrt(va1),
            Math.sqrt(1-va1)
        )
        var n_distance = radioTierra * va2

        if (n_distance < LIMIT_DISTANCE_ACCEPTED) distance += n_distance

        return n_distance
    }

    private fun checkPermission() : Boolean {
        return ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun runChrono() {
        mHandler?.let{
            chronometer.run()
        }
    }

    fun stopChrono() {
        mHandler!!.removeCallbacks(chronometer)
    }

    private fun checkStopRun(secs: Long){

        var seconds : Long = secs
        while(seconds > _intervalDuration.value) seconds -= _intervalDuration.value
        _timeRunning.value = getSecondsFromWatchUseCase(_homeUIState.value.intervalRunDuration).toLong()

        if(seconds ==_timeRunning.value){
            handleIntervalChange(true)
        } else {
            updateProgressBarRound(seconds)
        }

    }

    private fun checkNewRun(secs: Long){

        var seconds : Long = secs
        if(seconds.toInt() % _intervalDuration.value.toInt() == 0){
            if(secs > 0){
                _homeUIState.update { homeUIState ->
                    homeUIState.copy(
                        rounds = _homeUIState.value.rounds + 1
                    )
                }
                handleIntervalChange(false)
            }
        } else {
            updateProgressBarRound(seconds)
        }

    }

    override fun onCleared() {
        super.onCleared()
        audioProgressJob?.cancel()
        viewModelScope.launch {
            if (isAudioInitialized) {
                releaseAudioUseCase()
            }
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
        Log.d("HomeViewModel", "changeStarted called with: $b")

        _started.value = b
        _homeUIState.update { homeUIState ->
            homeUIState.copy(isStarted = b)
        }

        if(b){
            Log.d("HomeViewModel", "Starting training session")
            refreshInterfaceData()

            viewModelScope.launch {
                Log.d("HomeViewModel", "About to initialize audio...")
                initializeAudioUseCase().fold(
                    onSuccess = {
                        Log.d("HomeViewModel", "✅ Audio initialized successfully")
                        isAudioInitialized = true
                        updateAudioVolumes()
                        startAudioProgressTracking()

                        // NUEVO: SIEMPRE iniciar reproducción, independientemente del audioSwitch
                        Log.d("HomeViewModel", "Starting music playback (audioSwitch: ${_homeUIState.value.audioSwitch})")
                        Log.d("HomeViewModel", "isWalkingInterval: ${_isWalkingInterval.value}")

                        val audioModel = if (_isWalkingInterval.value) AudioModel.WALK else AudioModel.RUN
                        Log.d("HomeViewModel", "Selected audio model: $audioModel")

                        playAudioUseCase(PlayAudioUseCase.PlayAudioParams(audioModel)).fold(
                            onSuccess = {
                                Log.d("HomeViewModel", "✅ Music started successfully for $audioModel")
                            },
                            onFailure = { exception ->
                                Log.e("HomeViewModel", "❌ Failed to start music for $audioModel", exception)
                            }
                        )
                    },
                    onFailure = { exception ->
                        Log.e("HomeViewModel", "❌ Failed to initialize audio", exception)
                        isAudioInitialized = false
                    }
                )
            }
        } else {
            Log.d("HomeViewModel", "Stopping training session")
            setTotals()
            setRun()
            resetVariables()
        }
    }

    private fun setRun(){

        var id = _homeUIState.value.runId ?: ""
        var user = _homeUIState.value.user ?: ""

        viewModelScope.launch {
            setRunUseCase(
                SetRunSuspendResultUseCase.SetRunParams(
                    RunModel(
                        user,
                        id,
                        startDate,
                        startTime,
                        _homeUIState.value.kpiDuration,
                        _homeUIState.value. kpiDistance,
                        _homeUIState.value.kpiAvgSpeed,
                        _maxSpeed.value,
                        _homeUIState.value.kpiMinAltitude,
                        _homeUIState.value.kpiMaxAltitude,
                        _homeUIState.value.goalDurationSelected,
                        _homeUIState.value.goalHoursDefault,
                        _homeUIState.value.goalMinutesDefault,
                        _homeUIState.value.goalSecondsDefault,
                        _homeUIState.value.goalDistanceDefault,
                        _homeUIState.value.intervalDefault,
                        _homeUIState.value.intervalRunDuration,
                        _homeUIState.value.intervalWalkDuration,
                        _homeUIState.value.rounds
                    )
                )
            )
        }
    }


    private fun setTotals(){

        val recordAvgSpeed = _homeUIState.value.kpiRecordAvgSpeed
        val recordDistance = _homeUIState.value.kpiRecordDistance
        val recordSpeed = _homeUIState.value.kpiRecordSpeed
        val totalDistance = roundNumberUseCase((_homeUIState.value.kpiTotalDistance + _homeUIState.value.kpiDistance).toString(),2).toDouble()
        val totalRuns = _homeUIState.value.kpiTotalRuns + 1
        val totalTime = _totalTime.value + (getSecondsFromWatchUseCase(_homeUIState.value.kpiDuration) * 1000).toDouble()

        viewModelScope.launch {
            setTotalsUseCase(
                SetTotalsSuspendResultUseCase.SetTotalsParams(
                    TotalModel(
                        recordAvgSpeed,
                        recordDistance,
                        recordSpeed,
                        totalDistance,
                        totalRuns,
                        totalTime
                    )
                )
            )
        }
    }

    private fun resetVariables(){

        viewModelScope.launch {
            if (isAudioInitialized) {
                stopAllAudioUseCase().fold(
                    onSuccess = {
                        Log.d("HomeViewModel", "All audio stopped successfully")
                    },
                    onFailure = { exception ->
                        Log.e("HomeViewModel", "Failed to stop all audio", exception)
                    }
                )

                releaseAudioUseCase().fold(
                    onSuccess = {
                        isAudioInitialized = false
                        Log.d("HomeViewModel", "Audio resources released successfully")
                    },
                    onFailure = { exception ->
                        Log.e("HomeViewModel", "Failed to release audio resources", exception)
                    }
                )
            }
        }

        audioProgressJob?.cancel()

        distance = 0.0
        avgSpeed = 0.0
        speed = 0.0
        _maxSpeed.value = 0.00
        minLatitude = null
        maxLatitude = null
        minLongitude = null
        maxLongitude = null
        _coordinates.value = emptyList()
        timeInSeconds = 0
        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                kpiDuration = "00:00:00",
                rounds = 1,
                kpiDistance = 0.00,
                kpiMinAltitude = null,
                kpiMaxAltitude = null,
            )
        }
    }

    fun changeStopped(b: Boolean) {
        Log.d("HomeViewModel", "changeStopped called with: $b (true=stopped/paused, false=running)")

        _stopped.value = b

        viewModelScope.launch {
            Log.d("HomeViewModel", "About to handle running state change")
            Log.d("HomeViewModel", "isPaused: $b, isWalkingInterval: ${_isWalkingInterval.value}, audioEnabled: ${_homeUIState.value.audioSwitch}")

            handleRunningStateChangeUseCase(
                HandleRunningStateChangeUseCase.StateChangeParams(
                    isPaused = b,
                    isWalkingInterval = _isWalkingInterval.value
                )
            ).fold(
                onSuccess = {
                    Log.d("HomeViewModel", "Running state changed successfully")
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel", "Failed to change running state", exception)
                }
            )
        }
    }

    // CHANGES

    fun changePositionHardTrack(newPosition: Float) {

        if(!_started.value || _isWalkingInterval.value || !isAudioInitialized) return

        viewModelScope.launch {
            seekAudioUseCase(
                SeekAudioUseCase.SeekAudioParams(
                    audio = AudioModel.RUN,
                    position = newPosition
                )
            ).fold(
                onSuccess = {
                    Log.d("HomeViewModel","Run track position changed to $newPosition")
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel","Failed to seek run track", exception)
                }
            )
        }

    }

    fun changePositionSoftTrack(newPosition: Float) {

        if(!_started.value || _isWalkingInterval.value || !isAudioInitialized) return

        viewModelScope.launch {
            seekAudioUseCase(
                SeekAudioUseCase.SeekAudioParams(
                    audio = AudioModel.WALK,
                    position = newPosition
                )
            ).fold(
                onSuccess = {
                    Log.d("HomeViewModel","Walk track position changed to $newPosition")
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel","Failed to seek walk track", exception)
                }
            )
        }
    }

    fun changeLocationStatus(b: Boolean) {
        _locationStatus.value = b
    }

    fun changeMapType(b:Boolean){

            _homeUIState.update { homeUIState ->
                homeUIState.copy(
                    mapType = if(b) MapType.NORMAL else MapType.HYBRID
                )
            }

    }

    fun changeGoalSwitch(b: Boolean) {

        viewModelScope.launch {
            preferencesPutBooleanUseCase(SharedPreferencesKeys.SP_GOAL_SWITCH,b)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalSwitch = b
            )
        }

    }

    fun changeDurationSelected(b: Boolean) {

        viewModelScope.launch {
            preferencesPutBooleanUseCase(SharedPreferencesKeys.SP_DURATION_SELECTED,b)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalDurationSelected = b
            )
        }

    }

    fun changeHoursGoalDefault(i: Int) {

        viewModelScope.launch {
            preferencesPutIntUseCase(SharedPreferencesKeys.SP_HOURS_GOAL_DEFAULT,i)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalHoursDefault = i
            )
        }


    }

    fun changeMinutesGoalDefault(i: Int) {

        viewModelScope.launch {
            preferencesPutIntUseCase(SharedPreferencesKeys.SP_MINUTES_GOAL_DEFAULT,i)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalMinutesDefault = i
            )
        }

    }

    fun changeSecondsGoalDefault(i: Int) {

        viewModelScope.launch {
            preferencesPutIntUseCase(SharedPreferencesKeys.SP_SECONDS_GOAL_DEFAULT,i)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalSecondsDefault = i
            )
        }

    }

    fun changeKilometersGoalDefault(i: Int) {

        viewModelScope.launch {
            preferencesPutIntUseCase(SharedPreferencesKeys.SP_KILOMETERS_GOAL_DEFAULT,i)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalDistanceDefault = i
            )
        }

    }

    fun changeNotifyGoalCheck(b: Boolean) {

        viewModelScope.launch {
            preferencesPutBooleanUseCase(SharedPreferencesKeys.SP_NOTIFY_GOAL,b)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalNotifyCheck = b
            )
        }

    }

    fun changeAutomaticFinishCheck(b: Boolean) {

        viewModelScope.launch {
            preferencesPutBooleanUseCase(SharedPreferencesKeys.SP_AUTOMATIC_FINISH,b)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalAutomaticFinishCheck = b
            )
        }
    }

    fun changeIntervalSwitch(b: Boolean){

        viewModelScope.launch {
            preferencesPutBooleanUseCase(SharedPreferencesKeys.SP_INTERVAL_SWITCH,b)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                intervalSwitch = b
            )
        }

    }

    fun changeIntervalDuration(minutes: Long) {

        _intervalDuration.value = minutes * 60

        viewModelScope.launch {
            preferencesPutIntUseCase(SharedPreferencesKeys.SP_INTERVAL_DEFAULT,minutes.toInt() - 1)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                intervalDefault = minutes.toInt() - 1
            )
        }


    }

    fun changeIntervalDurationSeekbar(f: Float) {

        viewModelScope.launch {
            preferencesPutFloatUseCase(SharedPreferencesKeys.SP_INTERVAL_DURATION_SEEKBAR,f)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                intervalDurationSeekbar = f
            )
        }

    }

    fun changeAudioSwitch(b: Boolean) {

        viewModelScope.launch {
            preferencesPutBooleanUseCase(SharedPreferencesKeys.SP_AUDIO_SWITCH,b)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                audioSwitch = b
            )
        }

    }

    fun changeNotificationVolume(newVolume: Float) {

        viewModelScope.launch {
            preferencesPutFloatUseCase(SharedPreferencesKeys.SP_NOTIFICATION_VOLUME, newVolume)

            if (isAudioInitialized) {
                setAudioVolumeUseCase(
                    SetAudioVolumeUseCase.SetVolumeParams(
                        audio = AudioModel.NOTIFICATION,
                        volume = newVolume
                    )
                ).fold(
                    onSuccess = {
                        _homeUIState.update { it.copy(audioNotificationVolume = newVolume) }
                    },
                    onFailure = { exception ->
                        Log.e("HomeViewModel", "Failed to set notification volume", exception)
                    }
                )
            } else {
                _homeUIState.update { it.copy(audioNotificationVolume = newVolume) }
            }
        }

    }

    fun changeRunVolume(newVolume: Float) {

        viewModelScope.launch {
            preferencesPutFloatUseCase(SharedPreferencesKeys.SP_RUN_VOLUME, newVolume)

            if (isAudioInitialized) {
                setAudioVolumeUseCase(
                    SetAudioVolumeUseCase.SetVolumeParams(
                        audio = AudioModel.RUN,
                        volume = newVolume
                    )
                ).fold(
                    onSuccess = {
                        _homeUIState.update { it.copy(audioRunVolume = newVolume) }
                    },
                    onFailure = { exception ->
                        Log.e("HomeViewModel", "Failed to set run volume", exception)
                    }
                )
            } else {
                _homeUIState.update { it.copy(audioRunVolume = newVolume) }
            }
        }

    }

    fun changeWalkVolume(newVolume: Float) {

        viewModelScope.launch {
            preferencesPutFloatUseCase(SharedPreferencesKeys.SP_WALK_VOLUME, newVolume)

            if (isAudioInitialized) {
                setAudioVolumeUseCase(
                    SetAudioVolumeUseCase.SetVolumeParams(
                        audio = AudioModel.WALK,
                        volume = newVolume
                    )
                ).fold(
                    onSuccess = {
                        _homeUIState.update { it.copy(audioWalkVolume = newVolume) }
                    },
                    onFailure = { exception ->
                        Log.e("HomeViewModel", "Failed to set walk volume", exception)
                    }
                )
            } else {
                _homeUIState.update { it.copy(audioWalkVolume = newVolume) }
            }
        }
    }

    // GOOGLE MAPS

    // Whether or not to show all of the high peaks
    private var showAllCoordinates = MutableStateFlow(false)

    // Event channel to send events to the UI
    private val _eventChannel = Channel<HomeScreenEvent>()

    internal fun getEventChannel() = _eventChannel.receiveAsFlow()

    val homeScreenViewState =

        coordinates.combine(showAllCoordinates){ allCoordinates, showAllCoordinates ->
            if(allCoordinates.isEmpty()){
                HomeScreenViewState.Loading
            } else {

                val listOfLatLng = if (showAllCoordinates) allCoordinates.map { LatLng(it.latitude,it.longitude) } else allCoordinates.map { LatLng(it.latitude,it.longitude) }
                val boundingBox = LatLngBounds.Builder().apply {
                    listOfLatLng.forEach{ include(it)}
                }.build()
                HomeScreenViewState.LatLongList(
                        coordinates = allCoordinates,
                        boundingBox = boundingBox
                )

            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeScreenViewState.Loading
        )

    fun onEvent(event: HomeViewModelEvent){
        when(event){
            HomeViewModelEvent.OnZoomAll -> onZoomAll()
        }
    }

    private fun onZoomAll(){
        sendScreenEvent(HomeScreenEvent.OnZoomAll)
    }

    private fun sendScreenEvent(event: HomeScreenEvent){
        viewModelScope.launch { _eventChannel.send(event) }
    }

    fun showAllCoordinates(){
        showAllCoordinates.value = true
    }

    fun resetPreferences() : Boolean {
        var isSuccessful : Boolean = false
        viewModelScope.launch {
           isSuccessful = preferencesResetUseCase()
        }
        return isSuccessful
    }

}