package com.estholon.running.ui.screen.finished

sealed class FinishedScreenViewState {

    data class FinishedUIState(
        val mapIsLoading: Boolean = false,
        val totalDistance : Double = 0.00,
        val totalRuns : Double = 0.00,
        val totalTime : String = "0 d 0 h 0 m 0 s",

    ) : FinishedScreenViewState()

}