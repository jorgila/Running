package com.estholon.running.data.network.response

data class RunResponse (
    val user: String? = null,
    val runId: String? = null,
    val startDate : String? = null,
    val startTime : String? = null,
    val kpiDuration : String = "00:00:00",
    val kpiDistance : Double = 0.0,
    val kpiAvgSpeed : Double = 0.0,
    val kpiMaxSpeed : Double = 0.0,
    val kpiMinAltitude : Double? = null,
    val kpiMaxAltitude : Double? = null,
    val goalDurationSelected : Boolean = true,
    val goalHoursDefault : Int = 0,
    val goalMinutesDefault: Int = 0,
    val goalSecondsDefault : Int = 0,
    val goalDistanceDefault : Int = 0,
    val intervalDefault : Int = 0,
    val intervalRunDuration : String = "00:00",
    val intervalWalkDuration : String = "00:00",
    var rounds : Int = 1,
)