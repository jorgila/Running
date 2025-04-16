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
import kotlinx.coroutines.delay
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
import kotlin.math.round
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

    private val _timeRunning = MutableStateFlow<Long>(0)
    val timeRunning : StateFlow<Long> get() = _timeRunning

    private val _isWalkingInterval = MutableStateFlow<Boolean>(false)
    val isWalkingInterval : StateFlow<Boolean> get() = _isWalkingInterval

    private val _runningProgress = MutableStateFlow<Float>(0f)
    var runningProgress : StateFlow<Float> = _runningProgress

    private var mHandler : Handler? = null
    private var mInterval = 1000
    private var timeInSeconds = 0L


    private var mpNotify : MediaPlayer? = null
    private var mpHard : MediaPlayer? = null
    private var mpSoft : MediaPlayer? = null

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

    private var init_lt: Double = 0.0
    private var init_ln: Double = 0.0

    private var distance: Double = 0.0

    private val _maxSpeed = MutableStateFlow<Double>(0.0)
    val maxSpeed : StateFlow<Double> get() = _maxSpeed

    private var avgSpeed: Double = 0.0
    private var speed: Double = 0.0

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
    }

    fun initPreferences(){

        viewModelScope.launch {

            _homeUIState.update { homeUIState ->
                homeUIState.copy(
                    goalSwitch = preferencesGetBooleanUseCase.getBoolean(SharedPreferencesKeys.SP_GOAL_SWITCH,false),
                    goalDurationSelected = preferencesGetBooleanUseCase.getBoolean(SharedPreferencesKeys.SP_DURATION_SELECTED,true),
                    goalHoursDefault = preferencesGetIntUseCase.getInt(SharedPreferencesKeys.SP_HOURS_GOAL_DEFAULT,0),
                    goalMinutesDefault = preferencesGetIntUseCase.getInt(SharedPreferencesKeys.SP_MINUTES_GOAL_DEFAULT,0),
                    goalSecondsDefault = preferencesGetIntUseCase.getInt(SharedPreferencesKeys.SP_SECONDS_GOAL_DEFAULT,0),
                    goalDistanceDefault = preferencesGetIntUseCase.getInt(SharedPreferencesKeys.SP_KILOMETERS_GOAL_DEFAULT,0),
                    goalNotifyCheck = preferencesGetBooleanUseCase.getBoolean(SharedPreferencesKeys.SP_NOTIFY_GOAL,false),
                    goalAutomaticFinishCheck = preferencesGetBooleanUseCase.getBoolean(SharedPreferencesKeys.SP_AUTOMATIC_FINISH, false),
                    intervalSwitch = preferencesGetBooleanUseCase.getBoolean(SharedPreferencesKeys.SP_INTERVAL_SWITCH, false),
                    intervalDefault = preferencesGetIntUseCase.getInt(SharedPreferencesKeys.SP_INTERVAL_DEFAULT, 5),
                    intervalDurationSeekbar = preferencesGetFloatUseCase.getFloat(SharedPreferencesKeys.SP_INTERVAL_DURATION_SEEKBAR, 0.5F),
                    audioSwitch = preferencesGetBooleanUseCase.getBoolean(SharedPreferencesKeys.SP_AUDIO_SWITCH, false),
                    audioRunVolume = preferencesGetFloatUseCase.getFloat(SharedPreferencesKeys.SP_RUN_VOLUME, 70.0F),
                    audioWalkVolume = preferencesGetFloatUseCase.getFloat(SharedPreferencesKeys.SP_WALK_VOLUME, 70.0F),
                    audioNotificationVolume = preferencesGetFloatUseCase.getFloat(SharedPreferencesKeys.SP_NOTIFICATION_VOLUME, 70.0F),
                )
            }

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

                _homeUIState.update { homeUIState ->
                    homeUIState.copy(
                        kpiRecordAvgSpeed = totals.recordAvgSpeed,
                        kpiRecordDistance = totals.recordDistance,
                        kpiRecordSpeed = totals.recordSpeed,
                        kpiTotalDistance = totals.totalDistance,
                        kpiTotalRuns = totals.totalRuns,
                        kpiTotalTime = "$d d $h h $m m $s s"
                    )
                }
            }
        }
    }

    fun initLevels(){
        viewModelScope.launch {
            getLevelsUseCase.getLevels().collect{ levels ->
                delay(3000)
                for (level in levels) {

                    if( _homeUIState.value.kpiTotalDistance < level.distanceTarget || _homeUIState.value.kpiTotalRuns < level.runsTarget) {
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

    private fun setVolumes() {
        mpNotify?.setVolume(
            _homeUIState.value.audioNotificationVolume/100.0f,
            _homeUIState.value.audioNotificationVolume/100.0f
        )
        mpSoft?.setVolume(
            _homeUIState.value.audioWalkVolume/100.0f,
            _homeUIState.value.audioWalkVolume/100.0f
        )
        mpHard?.setVolume(
            _homeUIState.value.audioRunVolume/100.0f,
            _homeUIState.value.audioRunVolume/100.0f
        )
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
                val user = getUserInfoUseCase.getUserInfo()
                _homeUIState.update { homeUIState ->
                    homeUIState.copy(
                        user = user
                    )
                }

            }
        }
    }

    fun getIntervalRunDuration(interval: Long, seekbar: Float){

        val ms = ((interval * 60 * seekbar / 15.0).roundToInt() * 15 * 1000).toLong()

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                intervalRunDuration = getFormattedStopWatchUseCase.getFormattedStopWatch(ms)
            )
        }

    }

    fun getIntervalWalkDuration(interval: Long, seekbar: Float){

        val ms = ((interval * 60 * (1.0 - seekbar) / 15.0).roundToInt() * 15 * 1000).toLong()

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                intervalWalkDuration = getFormattedStopWatchUseCase.getFormattedStopWatch(ms)
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
                1f
            }
        }

        val kpiAvgSpeedFloat =
            if(_homeUIState.value.kpiAvgSpeed < _homeUIState.value.kpiRecordAvgSpeed) {
                (_homeUIState.value.kpiAvgSpeed / _homeUIState.value.kpiRecordAvgSpeed).toFloat()
            } else {
                1f
            }

        val kpiSpeedFloat =
            if(_homeUIState.value.kpiSpeed < _homeUIState.value.kpiRecordSpeed) {
                (_homeUIState.value.kpiSpeed / _homeUIState.value.kpiRecordSpeed).toFloat()
            } else {
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

                if(mpHard!!.isPlaying){

                    _homeUIState.update { homeUIState ->
                        homeUIState.copy(
                            audioRunTrack = (mpHard!!.currentPosition.toFloat() / mpHard!!.duration.toFloat())*100
                        )
                    }

                }

                if(mpSoft!!.isPlaying){

                    _homeUIState.update { homeUIState ->
                        homeUIState.copy(
                            audioWalkTrack = (mpSoft!!.currentPosition.toFloat() / mpSoft!!.duration.toFloat())*100
                        )
                    }

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

                if( homeUIState.value.intervalSwitch ){
                    checkStopRun(timeInSeconds)
                    checkNewRun(timeInSeconds)
                } else {
                    mpHard?.start()
                }
                timeInSeconds += 1

                _homeUIState.update { homeUIState ->
                    homeUIState.copy(
                        chrono = getFormattedStopWatchUseCase.getFormattedStopWatch(timeInSeconds*1000)
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
            if(_maxAltitude.value == null){
                _maxAltitude.value = location.altitude
                _minAltitude.value = location.altitude
            }
            if(location.altitude > _maxAltitude.value!!) _maxAltitude.value = location.altitude
            if(location.altitude < _minAltitude.value!!) _minAltitude.value = location.altitude
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

    fun resetChrono() {

        timeInSeconds = 0

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                chrono = "00:00:00",
                rounds = 1
            )
        }
    }

    private fun checkStopRun(secs: Long){
        var seconds : Long = secs
        while(seconds > _intervalDuration.value) seconds -= _intervalDuration.value
        _timeRunning.value = getSecondsFromWatchUseCase.getSecondsFromWatch(_homeUIState.value.intervalRunDuration).toLong()



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

                _homeUIState.update { homeUIState ->
                    homeUIState.copy(
                        rounds = _homeUIState.value.rounds + 1
                    )
                }

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

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                started = b
            )
        }

        if(b){
            refreshInterfaceData()
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
            // TODO
            _homeUIState.update { homeUIState ->
                homeUIState.copy(

                )
            }


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

            _homeUIState.update { homeUIState ->
                homeUIState.copy(
                    audioRunTrackPosition = getFormattedStopWatchUseCase.getFormattedStopWatch((mpHard!!.currentPosition).toLong()),
                    audioRunRemainingTrackPosition = getFormattedStopWatchUseCase.getFormattedStopWatch((mpHard!!.duration - mpHard!!.currentPosition).toLong())
                )
            }

        }
        if(timesS){

            _homeUIState.update { homeUIState ->
                homeUIState.copy(
                    audioWalkTrackPosition = getFormattedStopWatchUseCase.getFormattedStopWatch((mpSoft!!.currentPosition).toLong()),
                    audioWalkRemainingTrackPosition = getFormattedStopWatchUseCase.getFormattedStopWatch((mpSoft!!.duration - mpSoft!!.currentPosition).toLong())
                )
            }

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

            _homeUIState.update { homeUIState ->
                homeUIState.copy(
                    mapType = if(b) MapType.NORMAL else MapType.HYBRID
                )
            }

    }

    fun changeGoalSwitch(b: Boolean) {

        viewModelScope.launch {
            preferencesPutBooleanUseCase.putBoolean(SharedPreferencesKeys.SP_GOAL_SWITCH,b)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalSwitch = b
            )
        }

    }

    fun changeDurationSelected(b: Boolean) {

        viewModelScope.launch {
            preferencesPutBooleanUseCase.putBoolean(SharedPreferencesKeys.SP_DURATION_SELECTED,b)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalDurationSelected = b
            )
        }

    }

    fun changeHoursGoalDefault(i: Int) {

        viewModelScope.launch {
            preferencesPutIntUseCase.putInt(SharedPreferencesKeys.SP_HOURS_GOAL_DEFAULT,i)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalHoursDefault = i
            )
        }


    }

    fun changeMinutesGoalDefault(i: Int) {

        viewModelScope.launch {
            preferencesPutIntUseCase.putInt(SharedPreferencesKeys.SP_MINUTES_GOAL_DEFAULT,i)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalMinutesDefault = i
            )
        }

    }

    fun changeSecondsGoalDefault(i: Int) {

        viewModelScope.launch {
            preferencesPutIntUseCase.putInt(SharedPreferencesKeys.SP_SECONDS_GOAL_DEFAULT,i)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalSecondsDefault = i
            )
        }

    }

    fun changeKilometersGoalDefault(i: Int) {

        viewModelScope.launch {
            preferencesPutIntUseCase.putInt(SharedPreferencesKeys.SP_KILOMETERS_GOAL_DEFAULT,i)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalDistanceDefault = i
            )
        }

    }

    fun changeNotifyGoalCheck(b: Boolean) {

        viewModelScope.launch {
            preferencesPutBooleanUseCase.putBoolean(SharedPreferencesKeys.SP_NOTIFY_GOAL,b)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalNotifyCheck = b
            )
        }

    }

    fun changeAutomaticFinishCheck(b: Boolean) {

        viewModelScope.launch {
            preferencesPutBooleanUseCase.putBoolean(SharedPreferencesKeys.SP_AUTOMATIC_FINISH,b)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                goalAutomaticFinishCheck = b
            )
        }
    }

    fun changeIntervalSwitch(b: Boolean){

        viewModelScope.launch {
            preferencesPutBooleanUseCase.putBoolean(SharedPreferencesKeys.SP_INTERVAL_SWITCH,b)
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
            preferencesPutIntUseCase.putInt(SharedPreferencesKeys.SP_INTERVAL_DEFAULT,minutes.toInt() - 1)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                intervalDefault = minutes.toInt() - 1
            )
        }


    }

    fun changeIntervalDurationSeekbar(f: Float) {

        viewModelScope.launch {
            preferencesPutFloatUseCase.putFloat(SharedPreferencesKeys.SP_INTERVAL_DURATION_SEEKBAR,f)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                intervalDurationSeekbar = f
            )
        }

    }

    fun changeAudioSwitch(b: Boolean) {

        viewModelScope.launch {
            preferencesPutBooleanUseCase.putBoolean(SharedPreferencesKeys.SP_AUDIO_SWITCH,b)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                audioSwitch = b
            )
        }

    }

    fun changeNotificationVolume(newPosition: Float) {

        viewModelScope.launch {
            preferencesPutFloatUseCase.putFloat(SharedPreferencesKeys.SP_NOTIFICATION_VOLUME,newPosition)
        }

        _homeUIState.update {homeUIState ->
            homeUIState.copy(
                audioNotificationVolume = newPosition
            )
        }

        setVolumes()
    }

    fun changeRunVolume(newPosition: Float) {

        viewModelScope.launch {
            preferencesPutFloatUseCase.putFloat(SharedPreferencesKeys.SP_RUN_VOLUME,newPosition)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                audioRunVolume = newPosition
            )
        }

        setVolumes()
    }

    fun changeWalkVolume(newPosition: Float) {

        viewModelScope.launch {
            preferencesPutFloatUseCase.putFloat(SharedPreferencesKeys.SP_WALK_VOLUME,newPosition)
        }

        _homeUIState.update { homeUIState ->
            homeUIState.copy(
                audioWalkVolume = newPosition
            )
        }

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