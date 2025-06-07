package com.estholon.running.ui.screen.finished

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.R
import com.estholon.running.domain.model.TotalModel
import com.estholon.running.domain.useCase.firestore.DeleteLocationsUseCase
import com.estholon.running.domain.useCase.firestore.DeleteRunAndLinkedDataUseCase
import com.estholon.running.domain.useCase.firestore.GetAvgSpeedRecordUseCase
import com.estholon.running.domain.useCase.firestore.GetDistanceRecordUseCase
import com.estholon.running.domain.useCase.firestore.GetLevelsUseCase
import com.estholon.running.domain.useCase.firestore.GetRunUseCase
import com.estholon.running.domain.useCase.firestore.GetSpeedRecordUseCase
import com.estholon.running.domain.useCase.firestore.GetTotalsUseCase
import com.estholon.running.domain.useCase.firestore.SetTotalsUseCase
import com.estholon.running.domain.useCase.others.GetMillisecondsFromStringWithDHMSUseCase
import com.estholon.running.domain.useCase.others.GetSecondsFromWatchUseCase
import com.estholon.running.domain.useCase.others.GetStringWithDHMSFromMilisecondsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FinishedViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getTotalsUseCase: GetTotalsUseCase,
    private val setTotalsUseCase: SetTotalsUseCase,
    private val getLevelsUseCase: GetLevelsUseCase,
    private val getRunUseCase: GetRunUseCase,
    private val getDistanceRecordUseCase: GetDistanceRecordUseCase,
    private val getAvgSpeedRecordUseCase: GetAvgSpeedRecordUseCase,
    private val getSpeedRecordUseCase: GetSpeedRecordUseCase,
    private val deleteRunAndLinkedDataUseCase: DeleteRunAndLinkedDataUseCase,
    private val deleteLocationsUseCase: DeleteLocationsUseCase,
    private val getMillisecondsFromStringWithDHMSUseCase: GetMillisecondsFromStringWithDHMSUseCase,
    private val getStringWithDHMSFromMilisecondsUseCase: GetStringWithDHMSFromMilisecondsUseCase,
    private val getSecondsFromWatchUseCase: GetSecondsFromWatchUseCase,
) : ViewModel() {

    // UI STATE

    private val _finishedUIState = MutableStateFlow<FinishedScreenViewState.FinishedUIState>(FinishedScreenViewState.FinishedUIState())
    val finishedUIState : StateFlow<FinishedScreenViewState.FinishedUIState> = _finishedUIState

    init {
        initTotals()
        initLevels()
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

                        _finishedUIState.update { homeUIState ->
                            homeUIState.copy(
                                kpiTotalDistance = totals.totalDistance,
                                kpiTotalRuns = totals.totalRuns,
                                kpiTotalTime = "$d d $h h $m m $s s"
                            )
                        }

                    },
                    onFailure = { exception ->
                        Log.e("FinishedViewModel","Error loading totals", exception)
                    }
                )

            }
        }
    }

    private fun initLevels(){
        viewModelScope.launch {
            getLevelsUseCase.invoke().collect{ result ->
                result.fold(
                    onSuccess = { levels ->
                        for (level in levels) {

                            if( _finishedUIState.value.kpiTotalDistance < level.distanceTarget || _finishedUIState.value.kpiTotalRuns < level.runsTarget) {
                                _finishedUIState.update { homeUIState ->
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
                        Log.e("FinishedViewModel","Error loading levels", exception)
                    }
                )


            }
        }
    }

    fun initRun(id: String){

        viewModelScope.launch {
            getRunUseCase.invoke(id).collect{ result ->
                result.fold(
                    onSuccess = { run ->
                        _finishedUIState.update { finishedUIState ->
                            finishedUIState.copy(
                                user = run.user,
                                startDate = run.startDate,
                                startTime = run.startTime,
                                kpiDuration = run.kpiDuration,
                                kpiDistance = run.kpiDistance,
                                kpiAvgSpeed = run.kpiAvgSpeed,
                                kpiMaxSpeed = run.kpiMaxSpeed,
                                kpiMinAltitude = run.kpiMinAltitude,
                                kpiMaxAltitude = run.kpiMaxAltitude,
                                goalDurationSelected = run.goalDurationSelected,
                                goalHoursDefault = run.goalHoursDefault,
                                goalMinutesDefault = run.goalMinutesDefault,
                                goalSecondsDefault = run.goalSecondsDefault,
                                goalDistanceDefault = run.goalDistanceDefault,
                                intervalDefault = run.intervalDefault,
                                intervalRunDuration = run.intervalRunDuration,
                                intervalWalkDuration = run.intervalWalkDuration,
                                rounds = run.rounds,
                            )
                        }
                    },
                    onFailure = { exception ->
                        Log.e("FinishedViewModel","Error loading run", exception)
                    }
                )
            }
        }

    }

    fun deleteRunAndLinkedData(
        id: String
    ){

        viewModelScope.launch {
            try {
                deleteRunAndLinkedDataUseCase(id)
                    .onSuccess {
                        viewModelScope.launch {
                            processSuccessfulDeletion(true,id)
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

    private suspend fun processSuccessfulDeletion(boolean: Boolean, id: String) {

        var message : Boolean = boolean

        try {
            // Calculate new totals
            val newTotalDistance = _finishedUIState.value.kpiTotalDistance - _finishedUIState.value.kpiDistance
            val newTotalRuns = _finishedUIState.value.kpiTotalRuns - 1
            val newTotalTime = (getMillisecondsFromStringWithDHMSUseCase(_finishedUIState.value.kpiTotalTime)
                    - (getSecondsFromWatchUseCase(_finishedUIState.value.kpiDuration) * 1000)).toDouble()

            // Get Last Records
            val newDistanceRecord = getDistanceRecordSafely()
            val newAvgSpeedRecord = getAvgSpeedRecordSafely()
            val newSpeedRecord = getSpeedRecordSafely()

            // Update totals
            setTotalsUseCase(
                SetTotalsUseCase.Params(
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
            deleteLocationsUseCase(id)
                .onSuccess {
                    message = true
                }
                .onFailure {
                    message = false
                }

            // Update UI state
            _finishedUIState.update { finishedUIState ->
                finishedUIState.copy(
                    message = message,
                    kpiRecordAvgSpeed = newAvgSpeedRecord,
                    kpiRecordDistance = newDistanceRecord,
                    kpiRecordSpeed = newSpeedRecord,
                    kpiTotalDistance = newTotalDistance,
                    kpiTotalRuns = newTotalRuns,
                    kpiTotalTime = getStringWithDHMSFromMilisecondsUseCase(newTotalTime)
                )
            }



        } catch (e: Exception) {
            Log.e("FinishedViewModel",
                context.getString(
                    R.string.errormessage_error_processing_successful_deletion,
                    e.message
                ))
            Toast.makeText(
                context,
                context.getString(R.string.errormessage_error_updating_totals_after_deletion),
                Toast.LENGTH_LONG
            ).show()
            _finishedUIState.update { finishedUIState ->
                finishedUIState.copy(
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
                Log.e("FinishedViewModel",
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
                Log.e("FinishedViewModel",
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
                    "FinishedViewModel",
                    context.getString(
                        R.string.errormessage_exception_obtaining_speed_record,
                        e.message
                    )
                )
                0.0
            }
        }
    }
}