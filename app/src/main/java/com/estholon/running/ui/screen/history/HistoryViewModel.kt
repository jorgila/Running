package com.estholon.running.ui.screen.history

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.R
import com.estholon.running.domain.model.TotalModel
import com.estholon.running.domain.useCase.firestore.DeleteLocationsResultUseCase
import com.estholon.running.domain.useCase.firestore.DeleteRunAndLinkedDataResultUseCase
import com.estholon.running.domain.useCase.firestore.GetAllRunsResultUseCase
import com.estholon.running.domain.useCase.firestore.GetAvgSpeedRecordResultUseCase
import com.estholon.running.domain.useCase.firestore.GetDistanceRecordResultUseCase
import com.estholon.running.domain.useCase.firestore.GetLocationsResultUseCase
import com.estholon.running.domain.useCase.firestore.GetSpeedRecordResultUseCase
import com.estholon.running.domain.useCase.firestore.GetTotalsResultUseCase
import com.estholon.running.domain.useCase.firestore.SetTotalsSuspendResultUseCase
import com.estholon.running.domain.useCase.others.GetMillisecondsFromStringWithDHMSUseCase
import com.estholon.running.domain.useCase.others.GetSecondsFromWatchUseCase
import com.estholon.running.domain.useCase.others.GetStringWithDHMSFromMilisecondsUseCase
import com.estholon.running.domain.useCase.storage.DownloadImagesUseCase
import com.estholon.running.domain.useCase.storage.DownloadVideosUseCase
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

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getAllRunsUseCase: GetAllRunsResultUseCase,
    private val getTotalsUseCase: GetTotalsResultUseCase,
    private val setTotalsUseCase: SetTotalsSuspendResultUseCase,
    private val getDistanceRecordUseCase: GetDistanceRecordResultUseCase,
    private val getAvgSpeedRecordUseCase: GetAvgSpeedRecordResultUseCase,
    private val getSpeedRecordUseCase: GetSpeedRecordResultUseCase,
    private val deleteRunAndLinkedDataUseCase: DeleteRunAndLinkedDataResultUseCase,
    private val getLocationsResultUseCase: GetLocationsResultUseCase,
    private val deleteLocationsUseCase: DeleteLocationsResultUseCase,
    private val downloadImagesUseCase: DownloadImagesUseCase,
    private val downloadVideosUseCase: DownloadVideosUseCase,
    private val getMillisecondsFromStringWithDHMSUseCase: GetMillisecondsFromStringWithDHMSUseCase,
    private val getStringWithDHMSFromMilisecondsUseCase: GetStringWithDHMSFromMilisecondsUseCase,
    private val getSecondsFromWatchUseCase: GetSecondsFromWatchUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _historyUIState = MutableStateFlow<HistoryScreenViewState.HistoryUIState>(HistoryScreenViewState.HistoryUIState())
    val historyUIState : StateFlow<HistoryScreenViewState.HistoryUIState> get() = _historyUIState

    // GOOGLE MAP

    private val _coordinates = MutableStateFlow(emptyList<LatLng>())
    val coordinates: StateFlow<List<LatLng>> = _coordinates

    private val _runCoordinates = MutableStateFlow<Map<String, List<LatLng>>>(emptyMap())
    val runCoordinates: StateFlow<Map<String, List<LatLng>>> = _runCoordinates

    // IMAGES

    private val _runImages = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val runImages: StateFlow<Map<String, List<String>>> = _runImages

    // VIDEOS
    private val _runVideos = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val runVideos : StateFlow<Map<String, List<String>>> = _runVideos

    init {
        initRuns()
        initTotals()
    }

    private fun initRuns(){
        viewModelScope.launch {
            getAllRunsUseCase().collect(){ result ->
                result.fold(
                    onSuccess = { runs ->
                        _historyUIState.update { historyUIState ->
                            historyUIState.copy(
                                runs = runs
                            )
                        }
                    },
                    onFailure = { exception ->
                        Log.e("HistoryViewModel","Error loading runs", exception)
                    }
                )
            }
        }
    }

    private fun initTotals(){
        viewModelScope.launch {
            getTotalsUseCase.invoke().collect(){ result ->
                result.fold(
                    onSuccess = { totals ->
                        val segundosTotales = totals.totalTime / 1000
                        val minutosTotales = segundosTotales / 60
                        val horasTotales = minutosTotales / 60

                        val d = Math.floor(horasTotales / 24).toInt()
                        val h = Math.floor(horasTotales % 24).toInt()
                        val m = Math.floor(minutosTotales % 60).toInt()
                        val s = Math.floor(segundosTotales % 60).toInt()

                        _historyUIState.update { historyUIState ->
                            historyUIState.copy(
                                kpiTotalDistance = totals.totalDistance,
                                kpiTotalRuns = totals.totalRuns,
                                kpiTotalTime = "$d d $h h $m m $s s"
                            )
                        }

                    },
                    onFailure = { exception ->
                        Log.e("HistoryViewModel","Error loading totals", exception)
                    }
                )

            }
        }
    }



    fun deleteRunAndLinkedData(
        id: String,
        runDistance: Double,
        runDuration: String
    ){

        viewModelScope.launch {
            try {
                deleteRunAndLinkedDataUseCase(DeleteRunAndLinkedDataResultUseCase.DeleteRunParams(id))
                    .onSuccess {
                        viewModelScope.launch {
                            processSuccessfulDeletion(true,id,runDistance,runDuration)
                        }
                    }
            } catch (e: Exception){
                Toast.makeText(
                    context,
                    context.getString(R.string.errormessage_unexpected_error_when_deleting_the_race),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private suspend fun processSuccessfulDeletion(
        boolean: Boolean,
        id: String,
        runDistance: Double,
        runDuration: String
    ) {

        var message : Boolean = boolean

        try {
            // Calculate new totals
            val newTotalDistance = _historyUIState.value.kpiTotalDistance - runDistance
            val newTotalRuns = _historyUIState.value.kpiTotalRuns - 1
            val newTotalTime = (getMillisecondsFromStringWithDHMSUseCase(_historyUIState.value.kpiTotalTime)
                    - (getSecondsFromWatchUseCase(runDuration) * 1000)).toDouble()

            // Get Last Records
            val newDistanceRecord = getDistanceRecordSafely()
            val newAvgSpeedRecord = getAvgSpeedRecordSafely()
            val newSpeedRecord = getSpeedRecordSafely()

            // Update totals
            setTotalsUseCase(
                SetTotalsSuspendResultUseCase.SetTotalsParams(
                    TotalModel(
                        newAvgSpeedRecord,
                        newDistanceRecord,
                        newSpeedRecord,
                        newTotalDistance,
                        newTotalRuns,
                        newTotalTime
                    )
                )
            )

            // Delete locations
            deleteLocationsUseCase(DeleteLocationsResultUseCase.DeleteLocationsParams(id))
                .onSuccess {
                    message = true
                }
                .onFailure {
                    message = false
                }

            // Update UI state
            _historyUIState.update { historyUIState ->
                historyUIState.copy(
                    message = message,
                )
            }



        } catch (e: Exception) {
            Log.e("HistoryViewModel",
                context.getString(
                    R.string.errormessage_error_processing_successful_deletion,
                    e.message
                ))
            Toast.makeText(
                context,
                context.getString(R.string.errormessage_error_updating_totals_after_deletion),
                Toast.LENGTH_LONG
            ).show()
            _historyUIState.update { historyUIState ->
                historyUIState.copy(
                    message = boolean
                )
            }
        }
    }

    private suspend fun getDistanceRecordSafely(): Double {
        return withContext(Dispatchers.IO) {
            try {
                getDistanceRecordUseCase()
                    .getOrElse { exception ->
                        context.getString(R.string.errormessage_exception_obtaining_distance_record, exception.message)
                        0.0
                    }
            } catch (e: Exception) {
                Log.e("HistoryViewModel",
                    context.getString(
                        R.string.errormessage_exception_obtaining_distance_record,
                        e.message
                    ))
                0.0
            }
        }
    }

    private suspend fun getAvgSpeedRecordSafely(): Double {
        return withContext(Dispatchers.IO) {
            try {
                getAvgSpeedRecordUseCase()
                    .getOrElse { exception ->
                        context.getString(R.string.errormessage_exception_obtaining_avg_speed_record,exception.message)
                        0.0
                    }
            } catch (e: Exception) {
                Log.e("HistoryViewModel",
                    context.getString(
                        R.string.errormessage_exception_obtaining_avg_speed_record,
                        e.message
                    ))
                0.0
            }
        }
    }

    private suspend fun getSpeedRecordSafely(): Double {
        return withContext(Dispatchers.IO) {
            try {
                getSpeedRecordUseCase()
                    .getOrElse { exception ->
                        context.getString(R.string.errormessage_exception_obtaining_speed_record,exception.message)
                        0.0
                    }
            } catch (e: Exception) {
                Log.e(
                    "HistoryViewModel",
                    context.getString(
                        R.string.errormessage_exception_obtaining_speed_record,
                        e.message
                    )
                )
                0.0
            }
        }
    }

    fun getLocationsForRun(runId: String) {
        viewModelScope.launch {
            getLocationsResultUseCase(GetLocationsResultUseCase.GetLocationsParams(runId)).collect{ result ->
                result.fold(
                    onSuccess = { locations ->

                        val locationList = locations.map { location ->
                            LatLng(location.latitude,location.longitude)
                        }

                        _runCoordinates.update { currentMap ->
                            currentMap.toMutableMap().apply {
                                put(runId, locationList)
                            }
                        }

                    },
                    onFailure = { exception ->
                        Log.e("HistoryViewModel","Error loading locations", exception)
                    }
                )
            }
        }
    }

    fun areCoordinatesLoadedForRun(runId: String): Boolean {
        return _runCoordinates.value.containsKey(runId)
    }

    // Event channel to send events to the UI
    private val _eventChannel = Channel<HistoryScreenEvent>()

    internal fun getEventChannel() = _eventChannel.receiveAsFlow()


    fun getAllImages(runId: String) {
        if (_runImages.value.containsKey(runId)) {
            return
        }

        viewModelScope.launch {
            _historyUIState.value = _historyUIState.value.copy(isLoading = true)

            val result = withContext(Dispatchers.IO) {
                downloadImagesUseCase(runId).map { it.toString() }
            }

            _runImages.update { currentMap ->
                currentMap.toMutableMap().apply {
                    put(runId, result)
                }
            }

            _historyUIState.value = _historyUIState.value.copy(isLoading = false)
        }
    }

    fun getImagesForRun(runId: String): List<String> {
        return _runImages.value[runId] ?: emptyList()
    }

    fun getAllVideos(runId: String) {
        if (_runVideos.value.containsKey(runId)) {
            return
        }

        viewModelScope.launch {
            _historyUIState.update { it.copy(isLoading = true) }

            val result = withContext(Dispatchers.IO) {
                downloadVideosUseCase(runId).map { it.toString() }
            }

            _runVideos.update { currentMap ->
                currentMap.toMutableMap().apply {
                    put(runId, result)
                }
            }

            _historyUIState.update { it.copy(isLoading = false) }
        }
    }

    fun getVideosForRun(runId: String): List<String> {
        return _runVideos.value[runId] ?: emptyList()
    }

}