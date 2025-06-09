package com.estholon.running.ui.screen.finished

sealed class FinishedScreenViewState {

    data class FinishedUIState(
        val mapIsLoading: Boolean = false,
        val user: String? = null,
        val runId : String? = null,
        val startDate: String? = null,
        val startTime: String? = null,
        val kpiDuration: String = "00:00:00",
        val kpiDistance : Double = 0.0,
        val kpiRecordDistance : Double = 0.0,
        val kpiAvgSpeed : Double = 0.0,
        val kpiRecordAvgSpeed : Double = 0.0,
        val kpiSpeed : Double = 0.0,
        val kpiRecordSpeed : Double = 0.00,
        val kpiMaxSpeed: Double = 0.0,
        val kpiTotalDistance : Double = 0.00,
        val kpiTotalRuns : Double = 0.00,
        val kpiTotalTime : String = "0 d 0 h 0 m 0 s",
        val kpiLevel : String = "0",
        val kpiLevelDistance : Double = 0.00,
        val kpiLevelRuns : Double = 0.00,
        val kpiMinAltitude : Double? = null,
        val kpiMaxAltitude : Double? = null,
        val goalDurationSelected : Boolean = false,
        val goalHoursDefault : Int = 0,
        val goalMinutesDefault: Int = 0,
        val goalSecondsDefault : Int = 0,
        val goalDistanceDefault : Int = 0,
        val intervalDefault : Int = 0,
        val intervalRunDuration : String = "00:00",
        val intervalWalkDuration : String = "00:00",
        var rounds : Int = 1,
        val message : Boolean? = null,
    ) : FinishedScreenViewState()

}