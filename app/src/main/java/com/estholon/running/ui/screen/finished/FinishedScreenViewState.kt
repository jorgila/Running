package com.estholon.running.ui.screen.finished

sealed class FinishedScreenViewState {

    data class FinishedUIState(
        val mapIsLoading: Boolean = false,
        val kpiDistance : Double = 0.0,
        val kpiRecordDistance : Double = 0.0,
        val kpiAvgSpeed : Double = 0.0,
        val kpiRecordAvgSpeed : Double = 0.0,
        val kpiSpeed : Double = 0.0,
        val kpiRecordSpeed : Double = 0.00,
        val kpiTotalDistance : Double = 0.00,
        val kpiTotalRuns : Double = 0.00,
        val kpiTotalTime : String = "0 d 0 h 0 m 0 s",
        val kpiLevel : String = "0",
        val kpiLevelDistance : Double = 0.00,
        val kpiLevelRuns : Double = 0.00,
        val kpiMinAltitude : Double? = null,
        val kpiMaxAltitude : Double? = null,
        val chrono : String = "00:00:00",

    ) : FinishedScreenViewState()

}