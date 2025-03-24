package com.estholon.running.ui.screen.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.R
import com.estholon.running.common.Constants.INTERVAL_LOCATION
import com.estholon.running.common.Constants.LIMIT_DISTANCE_ACCEPTED
import com.estholon.running.domain.useCase.authentication.SignOutUseCase
import com.estholon.running.domain.useCase.others.GetFormattedStopWatchUseCase
import com.estholon.running.domain.useCase.others.GetSecondsFromWatchUseCase
import com.estholon.running.domain.useCase.others.GetUserInfoUseCase
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
import java.lang.Thread.State
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getSecondsFromWatchUseCase: GetSecondsFromWatchUseCase,
    private val getFormattedStopWatchUseCase: GetFormattedStopWatchUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        val REQUIRED_PERMISSIONS_GPS =
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
    }

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
    private var minAltitude: Double? = null
    private var maxAltitude: Double? = null

    private var init_lt: Double = 0.0
    private var init_ln: Double = 0.0

    private var distance: Double = 0.0
    private var maxSpeed: Double = 0.0
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

    init {
        getUserInfo()
        setKPI()
        mHandler = Handler()
        initPermissionGPS()
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
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper())

    }

    private val mLocationCallback = object: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {

            if(locationResult!=null) {
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
            if(maxAltitude == null){
                maxAltitude = location.altitude
                minAltitude = location.altitude
            }
            if(location.altitude > maxAltitude!!) maxAltitude = location.altitude
            if(location.altitude < minAltitude!!) minAltitude = location.altitude
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
        if(speed > maxSpeed) maxSpeed = speed
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
            minAltitude = null
            maxAltitude = null
            minLatitude = null
            maxLatitude = null
            minLongitude = null
            maxLongitude = null
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

}