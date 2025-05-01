package com.estholon.running.ui.screen.finished

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.estholon.running.R
import com.estholon.running.domain.useCase.firestore.DeleteRunAndLinkedDataUseCase
import com.estholon.running.domain.useCase.firestore.GetLevelsUseCase
import com.estholon.running.domain.useCase.firestore.GetTotalsUseCase
import com.estholon.running.domain.useCase.firestore.SetTotalsUseCase
import com.estholon.running.domain.useCase.others.GetMilisecondsFromStringWithDHMS
import com.estholon.running.domain.useCase.others.GetSecondsFromWatchUseCase
import com.estholon.running.ui.screen.home.HomeScreenViewState
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
    private val deleteRunAndLinkedDataUseCase: DeleteRunAndLinkedDataUseCase,
    private val getMilisecondsFromStringWithDHMS: GetMilisecondsFromStringWithDHMS,
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

                    if(boolean){
                        Log.e("FinishedViewModel PRUEBA PASO","PASA POR AQUÍ")
                        var newTotalDistance = _finishedUIState.value.kpiTotalDistance - _finishedUIState.value.kpiDistance
                        var newTotalRuns = _finishedUIState.value.kpiTotalRuns - 1
                        var newTotalTime = (getMilisecondsFromStringWithDHMS(_finishedUIState.value.kpiTotalTime) - (getSecondsFromWatchUseCase(_finishedUIState.value.chrono) * 1000)).toDouble()

                        viewModelScope.launch {
                            setTotalsUseCase(
                                0.0,
                                0.0,
                                0.0,
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
                                )
                            }
                        }


                    }



                }

            )
        }


    }

}