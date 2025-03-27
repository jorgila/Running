package com.estholon.running.ui.screen.finished

import android.content.Context
import androidx.lifecycle.ViewModel
import com.estholon.running.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FinishedViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {


    private var _isLoading = MutableStateFlow<Boolean>(false)
    var isLoading : StateFlow<Boolean> = _isLoading

    private var _level = MutableStateFlow<String>(context.getString(R.string.level_0))
    var level : StateFlow<String> = _level

    private var _totalTime = MutableStateFlow<String>(context.getString(R.string.total_0))
    var totalTime : StateFlow<String> = _totalTime

    private var _recordKilometers = MutableStateFlow<Double>(0.0 )
    var recordKilometers : StateFlow<Double> = _recordKilometers

    private var _recordAverageSpeed = MutableStateFlow<Double>(0.0)
    var recordAverageSpeed : StateFlow<Double> = _recordAverageSpeed

    private var _recordSpeed = MutableStateFlow<Double>(0.0)
    var recordSpeed : StateFlow<Double> = _recordSpeed

    private var _currentKilometers = MutableStateFlow<Double>(0.0 )
    var currentKilometers : StateFlow<Double> = _currentKilometers

    private var _totalKilometers = MutableStateFlow<Double>(0.0)
    var totalKilometers : StateFlow<Double> = _totalKilometers

    private var _currentRuns = MutableStateFlow<Int>(0)
    var currentRuns : StateFlow<Int> = _currentRuns

    private var _totalRuns = MutableStateFlow<Int>(0)
    var totalRuns : StateFlow<Int> = _totalRuns




}