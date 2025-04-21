package com.estholon.running.ui.screen.finished

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.estholon.running.R
import com.estholon.running.domain.useCase.firestore.GetTotalsUseCase
import com.estholon.running.domain.useCase.others.GetSecondsFromWatchUseCase
import com.estholon.running.ui.screen.home.HomeScreenViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinishedViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getTotalsUseCase: GetTotalsUseCase,
    private val getSecondsFromWatchUseCase: GetSecondsFromWatchUseCase,
) : ViewModel() {

    // UI STATE

    private val _finishedUIState = MutableStateFlow<FinishedScreenViewState.FinishedUIState>(FinishedScreenViewState.FinishedUIState())
    val finishedUIState : StateFlow<FinishedScreenViewState.FinishedUIState> = _finishedUIState

    // VARIABLES

    private var _isLoading = MutableStateFlow<Boolean>(false)
    var isLoading : StateFlow<Boolean> = _isLoading

    private var _level = MutableStateFlow<String>(context.getString(R.string.level_0))
    var level : StateFlow<String> = _level

    private var _totalKilometers = MutableStateFlow<Double>(0.0)
    var totalKilometers : StateFlow<Double> = _totalKilometers

    private var _totalRuns = MutableStateFlow<Int>(0)
    var totalRuns : StateFlow<Int> = _totalRuns

    init {
        initTotals()
    }

    fun initTotals(){
        viewModelScope.launch {
            getTotalsUseCase.getTotals().collect{ totals ->

                Log.e("FinishedViewModel Chrono",_finishedUIState.value.chrono)
                Log.e("FinishedViewModel Distance",_finishedUIState.value.kpiDistance.toString())

                val currentSeconds = getSecondsFromWatchUseCase(_finishedUIState.value.chrono)
                val currentTime = currentSeconds * 1000

                val segundosTotales = (totals.totalTime + currentTime ) / 1000
                val minutosTotales = segundosTotales / 60
                val horasTotales = minutosTotales / 60

                val d = Math.floor(horasTotales / 24).toInt()
                val h = Math.floor(horasTotales % 24).toInt()
                val m = Math.floor(minutosTotales % 60).toInt()
                val s = Math.floor(segundosTotales % 60).toInt()

                val currentDistance = _finishedUIState.value.kpiDistance

                _finishedUIState.update { homeUIState ->
                    homeUIState.copy(
                        kpiTotalDistance = totals.totalDistance + currentDistance,
                        kpiTotalRuns = totals.totalRuns + 1,
                        kpiTotalTime = "$d d $h h $m m $s s"
                    )
                }
            }
        }
    }

}