package com.estholon.running.ui.screen.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.R
import com.estholon.running.common.Constants.INTERVAL_LOCATION
import com.estholon.running.common.Constants.LIMIT_DISTANCE_ACCEPTED
import com.estholon.running.common.SharedPreferencesKeys
import com.estholon.running.domain.useCase.authentication.SignOutUseCase
import com.estholon.running.domain.useCase.firestore.GetLevelsUseCase
import com.estholon.running.domain.useCase.firestore.GetTotalsUseCase
import com.estholon.running.domain.useCase.others.GetFormattedStopWatchUseCase
import com.estholon.running.domain.useCase.others.GetSecondsFromWatchUseCase
import com.estholon.running.domain.useCase.others.GetUserInfoUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesGetBooleanUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesGetFloatUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesGetIntUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesPutBooleanUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesPutFloatUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesPutIntUseCase
import com.estholon.running.domain.useCase.sharedPreferences.PreferencesResetUseCase
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getSecondsFromWatchUseCase: GetSecondsFromWatchUseCase,
    private val getFormattedStopWatchUseCase: GetFormattedStopWatchUseCase,
    private val preferencesGetBooleanUseCase: PreferencesGetBooleanUseCase,
    private val preferencesPutBooleanUseCase: PreferencesPutBooleanUseCase,
    private val preferencesGetFloatUseCase: PreferencesGetFloatUseCase,
    private val preferencesPutFloatUseCase: PreferencesPutFloatUseCase,
    private val preferencesGetIntUseCase: PreferencesGetIntUseCase,
    private val preferencesPutIntUseCase: PreferencesPutIntUseCase,
    private val preferencesResetUseCase: PreferencesResetUseCase,
    private val getTotalsUseCase: GetTotalsUseCase,
    private val getLevelsUseCase: GetLevelsUseCase,
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

    // VARIABLES

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

    private val _currentKilometers = MutableStateFlow<Double>(0.0 )
    var currentKilometers : StateFlow<Double> = _currentKilometers

    private val _currentAverageSpeed = MutableStateFlow<Double>(0.0 )
    var currentAverageSpeed : StateFlow<Double> = _currentAverageSpeed

    private val _currentSpeed = MutableStateFlow<Double>(0.0 )
    var currentSpeed : StateFlow<Double> = _currentSpeed

    private val _recordKilometers = MutableStateFlow<Double>(0.0 )
    var recordKilometers : StateFlow<Double> = _recordKilometers

    private val _recordAverageSpeed = MutableStateFlow<Double>(0.0)
    var recordAverageSpeed : StateFlow<Double> = _recordAverageSpeed

    private val _recordSpeed = MutableStateFlow<Double>(0.0)
    var recordSpeed : StateFlow<Double> = _recordSpeed

    private val _goalKilometers = MutableStateFlow<Double>(0.0)
    var goalKilometers : StateFlow<Double> = _goalKilometers

    private val _totalDistance = MutableStateFlow<Double>(0.0)
    var totalDistance : StateFlow<Double> = _totalDistance

    private val _totalRuns = MutableStateFlow<Double>(0.0)
    var totalRuns : StateFlow<Double> = _totalRuns

    private val _secondsFromWatch = MutableStateFlow<Int>(0)
    var secondsFromWatch : StateFlow<Int> = _secondsFromWatch

    private val _runIntervalDuration = MutableStateFlow("00:00")
    var runIntervalDuration : StateFlow<String> = _runIntervalDuration

    private val _walkIntervalDuration = MutableStateFlow("00:00")
    var walkIntervalDuration : StateFlow<String> = _walkIntervalDuration

    private val _chrono = MutableStateFlow<String>("00:00:00")
    var chrono : StateFlow<String> = _chrono


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

    private val _locationStatus = MutableStateFlow<Boolean>(false)
    val locationStatus : StateFlow<Boolean> get() = _locationStatus

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val PERMISSION_ID = 42
    private var flagSavedLocation = false
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var minLatitude: Double? = null
    private var maxLatitude: Double? = null
    private var minLongitude: Double? = null
    private var maxLongitude: Double? = null

    private val _minAltitude = MutableStateFlow<Double?>(null)
    val minAltitude : StateFlow<Double?> get() = _minAltitude

    private val _maxAltitude = MutableStateFlow<Double?>(null)
    val maxAltitude : StateFlow<Double?> get() = _maxAltitude

    private val _distanceGoal = MutableStateFlow<Int>(0)
    val distanceGoal : StateFlow<Int> get() = _distanceGoal

    private var init_lt: Double = 0.0
    private var init_ln: Double = 0.0

    private var distance: Double = 0.0

    private val _maxSpeed = MutableStateFlow<Double>(0.0)
    val maxSpeed : StateFlow<Double> get() = _maxSpeed

    private var avgSpeed: Double = 0.0
    private var speed: Double = 0.0

    // MAP

    private val _latlng = MutableStateFlow<LatLng>(LatLng(0.0,0.0))
    val latlng : StateFlow<LatLng> get() = _latlng

    private val _mapType = MutableStateFlow<MapType>(MapType.NORMAL)
    val mapType : StateFlow<MapType> get() = _mapType

    // KPIs

    private val _kilometersKPI = MutableStateFlow<Float>(0f)
    var kilometersKPI : StateFlow<Float> = _kilometersKPI

    private val _speedKPI = MutableStateFlow<Float>(0f)
    var speedKPI : StateFlow<Float> = _speedKPI

    private val _avgSpeedKPI = MutableStateFlow<Float>(0f)
    var avgSpeedKPI : StateFlow<Float> = _avgSpeedKPI

    // GOOGLE MAP

    private val _coordinates = MutableStateFlow(emptyList<LatLng>())
    val coordinates: StateFlow<List<LatLng>> = _coordinates

    // UI

    //// GOAL SETTINGS

    private val _goalSwitch = MutableStateFlow<Boolean>(false)
    val goalSwitch: StateFlow<Boolean> = _goalSwitch

    private val _durationSelected = MutableStateFlow<Boolean>(true)
    val durationSelected: StateFlow<Boolean> = _durationSelected

    private val _hoursGoalDefault = MutableStateFlow<Int>(0)
    var hoursGoalDefault : StateFlow<Int> = _hoursGoalDefault

    private val _minutesGoalDefault = MutableStateFlow<Int>(0)
    var minutesGoalDefault : StateFlow<Int> = _minutesGoalDefault

    private val _secondsGoalDefault = MutableStateFlow<Int>(0)
    var secondsGoalDefault : StateFlow<Int> = _secondsGoalDefault

    private val _durationGoal = MutableStateFlow<String>("00:00:00")
    var durationGoal : StateFlow<String> = _durationGoal


    private val _kilometersGoalDefault = MutableStateFlow<Int>(0)
    var kilometersGoalDefault : StateFlow<Int> = _kilometersGoalDefault



    private val _notifyGoalCheck = MutableStateFlow<Boolean>(false)
    val notifyGoalCheck : StateFlow<Boolean> get() = _notifyGoalCheck

    private val _automaticFinishCheck = MutableStateFlow<Boolean>(false)
    val automaticFinishCheck : StateFlow<Boolean> get() = _automaticFinishCheck

    //// INTERVAL SETTINGS

    private val _intervalSwitch = MutableStateFlow<Boolean>(false)
    val intervalSwitch : StateFlow<Boolean> get() = _intervalSwitch

    private val _intervalDuration = MutableStateFlow<Long>(1)
    val intervalDuration : StateFlow<Long> get() = _intervalDuration

    private val _intervalDurationSeekbar = MutableStateFlow<Float>(0.5F)
    val intervalDurationSeekbar : StateFlow<Float> get() = _intervalDurationSeekbar

    private val _intervalDefault = MutableStateFlow<Int>(4)
    val intervalDefault : StateFlow<Int> get() = _intervalDefault

    //// AUDIO SETTINGS

    private val _audioSwitch = MutableStateFlow<Boolean>(false)
    val audioSwitch: StateFlow<Boolean> = _audioSwitch

    private val _runVolume = MutableStateFlow<Float>(70.0f)
    val runVolume : StateFlow<Float> get() = _runVolume

    private val _walkVolume = MutableStateFlow<Float>(70.0f)
    val walkVolume : StateFlow<Float> get() = _walkVolume

    private val _notificationVolume = MutableStateFlow<Float>(70.0f)
    val notificationVolume : StateFlow<Float> get() = _notificationVolume


    init {
        getUserInfo()
        setKPI()
        mHandler = Handler()
        initPermissionGPS()
        initPreferences()
        initTotals()
        initLevels()
    }

    fun initPreferences(){

        viewModelScope.launch {
            _goalSwitch.value = preferencesGetBooleanUseCase.getBoolean(SharedPreferencesKeys.SP_GOAL_SWITCH,false)
            _durationSelected.value = preferencesGetBooleanUseCase.getBoolean(SharedPreferencesKeys.SP_DURATION_SELECTED,true)
            _hoursGoalDefault.value = preferencesGetIntUseCase.getInt(SharedPreferencesKeys.SP_HOURS_GOAL_DEFAULT,0)
            _minutesGoalDefault.value = preferencesGetIntUseCase.getInt(SharedPreferencesKeys.SP_MINUTES_GOAL_DEFAULT,0)
            _secondsGoalDefault.value = preferencesGetIntUseCase.getInt(SharedPreferencesKeys.SP_SECONDS_GOAL_DEFAULT,0)
            _kilometersGoalDefault.value = preferencesGetIntUseCase.getInt(SharedPreferencesKeys.SP_KILOMETERS_GOAL_DEFAULT,0)
            _notifyGoalCheck.value = preferencesGetBooleanUseCase.getBoolean(SharedPreferencesKeys.SP_NOTIFY_GOAL,false)
            _automaticFinishCheck.value = preferencesGetBooleanUseCase.getBoolean(SharedPreferencesKeys.SP_AUTOMATIC_FINISH, false)
            _intervalSwitch.value = preferencesGetBooleanUseCase.getBoolean(SharedPreferencesKeys.SP_INTERVAL_SWITCH, false)
            _intervalDefault.value = preferencesGetIntUseCase.getInt(SharedPreferencesKeys.SP_INTERVAL_DEFAULT, 5)
            _intervalDurationSeekbar.value = preferencesGetFloatUseCase.getFloat(SharedPreferencesKeys.SP_INTERVAL_DURATION_SEEKBAR, 0.5F)
            _audioSwitch.value = preferencesGetBooleanUseCase.getBoolean(SharedPreferencesKeys.SP_AUDIO_SWITCH, false)
            _runVolume.value = preferencesGetFloatUseCase.getFloat(SharedPreferencesKeys.SP_RUN_VOLUME, 70.0F)
            _walkVolume.value = preferencesGetFloatUseCase.getFloat(SharedPreferencesKeys.SP_WALK_VOLUME, 70.0F)
            _notificationVolume.value = preferencesGetFloatUseCase.getFloat(SharedPreferencesKeys.SP_NOTIFICATION_VOLUME, 70.0F)
        }

    }

    fun initTotals(){

        viewModelScope.launch {
            getTotalsUseCase.getTotals().collect{ totals ->

                val segundosTotales = totals.totalTime / 1000
                val minutosTotales = segundosTotales / 60
                val horasTotales = minutosTotales / 60

                val d = Math.floor(horasTotales / 24).toInt()
                val h = Math.floor(horasTotales % 24).toInt()
                val m = Math.floor(minutosTotales % 60).toInt()
                val s = Math.floor(segundosTotales % 60).toInt()

                _totalDistance.value = totals.totalDistance
                _totalRuns.value = totals.totalRuns

                _homeUIState.update { homeUIState ->
                    homeUIState.copy(
                    recordAvgSpeed = totals.recordAvgSpeed,
                    recordDistance = totals.recordDistance,
                    recordSpeed = totals.recordSpeed,
                    totalDistance = totals.totalDistance,
                    totalRuns = totals.totalRuns,
                    totalTime = "$d d $h h $m m $s s"
                    )
                }
            }
        }
    }

    fun initLevels(){
        viewModelScope.launch {
            getLevelsUseCase.getLevels().collect{ levels ->

                for (level in levels) {
                    if(  _totalDistance.value < level.distanceTarget || _totalRuns.value < level.runsTarget) {
                        _homeUIState.update { homeUIState ->
                            homeUIState.copy(
                                level = level.level,
                                levelDistance = level.distanceTarget,
                                levelRuns = level.runsTarget
                            )
                        }
                    }
                    break
                }

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
        _currentKilometers.value = 100.0
    }

    private fun allPermissionsGrantedGPS() = REQUIRED_PERMISSIONS_GPS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
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

    fun getRunIntervalDuration(interval: Long, seekbar: Float){
        // TODO: Redondear de 15 en 15 segundos
        val ms = (interval * 1000 * 60 * seekbar).toLong()
        _runIntervalDuration.value = getFormattedStopWatchUseCase.getFormattedStopWatch(ms)
    }

    fun getWalkIntervalDuration(interval: Long, seekbar: Float){
        // TODO: Redondear de 15 en 15 segundos
        val ms = (interval * 1000 * 60 * (1.0 - seekbar)).toLong()
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

                if(timeInSeconds==0L){
                    flagSavedLocation = false
                    manageLocation()
                    flagSavedLocation = true
                    manageLocation()
                }

                if(
                    _locationStatus.value
                    && timeInSeconds.toInt() % INTERVAL_LOCATION == 0
                ) manageLocation()

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
                        _coordinates.update {
                            it + LatLng(new_latitude,new_longitude)
                        }
                        _latlng.value = LatLng(new_latitude,new_longitude)
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
            if(_maxAltitude.value == null){
                _maxAltitude.value = location.altitude
                _minAltitude.value = location.altitude
            }
            if(location.altitude > _maxAltitude.value!!) _maxAltitude.value = location.altitude
            if(location.altitude < _minAltitude.value!!) _minAltitude.value = location.altitude
        }
    }

    private fun refreshInterfaceData() {
        _currentKilometers.value = Math.round(distance * 100).toDouble()/100
        _currentAverageSpeed.value = Math.round(avgSpeed * 10).toDouble()/10
        _currentSpeed.value = Math.round(speed * 10).toDouble()/10
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

    fun resetChrono() {

        timeInSeconds = 0
        _rounds.value = 1
        _chrono.value = "00:00:00"

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
            refreshInterfaceData()
            if(mpNotify==null){
                initMusic()
            }

            viewModelScope.launch {
                preferencesPutBooleanUseCase.putBoolean(SharedPreferencesKeys.SP_GOAL_SWITCH,_goalSwitch.value)
                preferencesPutBooleanUseCase.putBoolean(SharedPreferencesKeys.SP_DURATION_SELECTED,_durationSelected.value)
                preferencesPutIntUseCase.putInt(SharedPreferencesKeys.SP_HOURS_GOAL_DEFAULT,_hoursGoalDefault.value)
                preferencesPutIntUseCase.putInt(SharedPreferencesKeys.SP_MINUTES_GOAL_DEFAULT,_minutesGoalDefault.value)
                preferencesPutIntUseCase.putInt(SharedPreferencesKeys.SP_SECONDS_GOAL_DEFAULT,_secondsGoalDefault.value)
                preferencesPutIntUseCase.putInt(SharedPreferencesKeys.SP_KILOMETERS_GOAL_DEFAULT,_kilometersGoalDefault.value)
                preferencesPutBooleanUseCase.putBoolean(SharedPreferencesKeys.SP_NOTIFY_GOAL,_notifyGoalCheck.value)
                preferencesPutBooleanUseCase.putBoolean(SharedPreferencesKeys.SP_AUTOMATIC_FINISH,_automaticFinishCheck.value)
                preferencesPutBooleanUseCase.putBoolean(SharedPreferencesKeys.SP_INTERVAL_SWITCH,_intervalSwitch.value)
                preferencesPutIntUseCase.putInt(SharedPreferencesKeys.SP_INTERVAL_DEFAULT,_intervalDefault.value)
                preferencesPutFloatUseCase.putFloat(SharedPreferencesKeys.SP_INTERVAL_DURATION_SEEKBAR,_intervalDurationSeekbar.value)
                preferencesPutBooleanUseCase.putBoolean(SharedPreferencesKeys.SP_AUDIO_SWITCH,_audioSwitch.value)
                preferencesPutFloatUseCase.putFloat(SharedPreferencesKeys.SP_RUN_VOLUME,_runVolume.value)
                preferencesPutFloatUseCase.putFloat(SharedPreferencesKeys.SP_WALK_VOLUME,_walkVolume.value)
                preferencesPutFloatUseCase.putFloat(SharedPreferencesKeys.SP_NOTIFICATION_VOLUME,_notificationVolume.value)
            }

        } else {
            mpHard?.stop()
            mpSoft?.stop()
            mpNotify?.stop()
            mpNotify = null
            mpHard = null
            mpSoft = null
            distance = 0.0
            avgSpeed = 0.0
            speed = 0.0
            _minAltitude.value = null
            _maxAltitude.value = null
            minLatitude = null
            maxLatitude = null
            minLongitude = null
            maxLongitude = null
            _coordinates.value = emptyList()
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

    // CHANGES

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

    fun changeLocationStatus(b: Boolean) {
        _locationStatus.value = b
    }

    fun changeMapType(b:Boolean){
        if(b){
            _mapType.value = MapType.NORMAL
        } else {
            _mapType.value = MapType.HYBRID
        }
    }

    fun changeGoalSwitch(b: Boolean) {
        _goalSwitch.value = b
    }

    fun changeDurationSelected(b: Boolean) {
        _durationSelected.value = b
    }

    fun changeHoursGoalDefault(i: Int) {
        _hoursGoalDefault.value = i
    }

    fun changeMinutesGoalDefault(i: Int) {
        _minutesGoalDefault.value = i
    }

    fun changeSecondsGoalDefault(i: Int) {
        _secondsGoalDefault.value = i
    }

    fun changeDurationGoal(s: String) {
        _durationGoal.value = s
    }

    fun changeKilometersGoalDefault(i: Int) {
        _kilometersGoalDefault.value = i
        _distanceGoal.value = i
    }

    fun changeNotifyGoalCheck(b: Boolean) {
        _notifyGoalCheck.value = b
    }

    fun changeAutomaticFinishCheck(b: Boolean) {
        _automaticFinishCheck.value = b
    }

    fun changeIntervalSwitch(b: Boolean){
        _intervalSwitch.value = b
    }

    fun changeIntervalDuration(minutes: Long) {
        _intervalDuration.value = minutes * 60
        _intervalDefault.value = minutes.toInt() - 1
    }

    fun changeIntervalDurationSeekbar(f: Float) {
        _intervalDurationSeekbar.value = f
    }

    fun changeAudioSwitch(b: Boolean) {
        _audioSwitch.value = b
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
           isSuccessful = preferencesResetUseCase.resetPreferences()
        }
        return isSuccessful
    }


}