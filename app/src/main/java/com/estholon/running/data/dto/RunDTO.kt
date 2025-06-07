package com.estholon.running.data.dto

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapType

data class RunDTO(
    val user: String,
    val runId: String,
    val startDate : String,
    val startTime : String,
    val kpiDuration : String,
    val kpiDistance : Double,
    val kpiAvgSpeed : Double,
    val kpiMaxSpeed : Double,
    val kpiMinAltitude : Double?,
    val kpiMaxAltitude : Double?,
    val goalDurationSelected : Boolean,
    val goalHoursDefault : Int,
    val goalMinutesDefault: Int,
    val goalSecondsDefault : Int,
    val goalDistanceDefault : Int,
    val intervalDefault : Int,
    val intervalRunDuration : String,
    val intervalWalkDuration : String,
    var rounds : Int,
)