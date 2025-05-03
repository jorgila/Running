package com.estholon.running.ui.screen.finished

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.running.domain.useCase.firestore.DeleteRunAndLinkedDataUseCase
import com.estholon.running.domain.useCase.firestore.GetAvgSpeedRecordUseCase
import com.estholon.running.domain.useCase.firestore.GetDistanceRecordUseCase
import com.estholon.running.domain.useCase.firestore.GetLevelsUseCase
import com.estholon.running.domain.useCase.firestore.GetRunUseCase
import com.estholon.running.domain.useCase.firestore.GetSpeedRecordUseCase
import com.estholon.running.domain.useCase.firestore.GetTotalsUseCase
import com.estholon.running.domain.useCase.firestore.SetTotalsUseCase
import com.estholon.running.domain.useCase.others.GetMilisecondsFromStringWithDHMSUseCase
import com.estholon.running.domain.useCase.others.GetSecondsFromWatchUseCase
import com.estholon.running.domain.useCase.others.GetStringWithDHMSFromMilisecondsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val getMilisecondsFromStringWithDHMSUseCase: GetMilisecondsFromStringWithDHMSUseCase,
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
            getTotalsUseCase.invoke().collect(){ totals ->

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
            }
        }
    }

    private fun initLevels(){
        viewModelScope.launch {
            getLevelsUseCase.invoke().collect{ levels ->
                delay(3000)
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

            }
        }
    }

    fun initRun(id: String){

        viewModelScope.launch {
            getRunUseCase.invoke(id).collect{ run ->
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
            }
        }

    }

    fun deleteRunAndLinkedData(
        id: String
    ){

        viewModelScope.launch {
            deleteRunAndLinkedDataUseCase(
                id,
                { boolean ->
                    _finishedUIState.update { finishedUIState ->
                        finishedUIState.copy(
                            message = boolean
                        )
                    }

                    var newDistanceRecord = 0.0
                    var newAvgSpeedRecord = 0.0
                    var newSpeedRecord = 0.0

                        if(boolean){

                        var newTotalDistance = _finishedUIState.value.kpiTotalDistance - _finishedUIState.value.kpiDistance
                        var newTotalRuns = _finishedUIState.value.kpiTotalRuns - 1
                        var newTotalTime =
                            (getMilisecondsFromStringWithDHMSUseCase(_finishedUIState.value.kpiTotalTime)
                            - (getSecondsFromWatchUseCase(_finishedUIState.value.kpiDuration) * 1000)
                            ).toDouble()

                        viewModelScope.launch {

                            newDistanceRecord = getDistanceRecordUseCase(
                                { isSuccessful ->
                                    if(!isSuccessful){
                                        // TODO Message in UI
                                    }
                                }
                            )
                            newAvgSpeedRecord = getAvgSpeedRecordUseCase(
                                { isSuccessful ->
                                    if(!isSuccessful){
                                        // TODO Message in UI
                                    }
                                }
                            )
                            newSpeedRecord = getSpeedRecordUseCase(
                                { isSuccessful ->
                                    if(!isSuccessful){
                                        // TODO Message in UI
                                    }
                                }
                            )
                            delay(3000)
                            setTotalsUseCase(
                                newAvgSpeedRecord,
                                newDistanceRecord,
                                newSpeedRecord,
                                newTotalDistance,
                                newTotalRuns,
                                newTotalTime
                            )

                            _finishedUIState.update { finishedUIState ->
                                finishedUIState.copy(
                                    kpiRecordAvgSpeed = 0.0,
                                    kpiRecordDistance = 0.0,
                                    kpiRecordSpeed = 0.0,
                                    kpiTotalDistance = newTotalDistance,
                                    kpiTotalRuns = newTotalRuns,
                                    kpiTotalTime = getStringWithDHMSFromMilisecondsUseCase(newTotalTime)
                                )
                            }
                        }


                    }



                }

            )
        }


    }

}