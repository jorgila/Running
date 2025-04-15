package com.estholon.running.ui.screen.home

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

sealed class HomeScreenViewState {

    data object Loading : HomeScreenViewState()

    data class LatLongList(
        val coordinates: List<LatLng>,
        val boundingBox: LatLngBounds
    ) : HomeScreenViewState()

    data class HomeUIState(
        val mapIsLoading: Boolean = false,
        val recordAvgSpeed : Double = 0.0,
        val recordDistance : Double = 0.0,
        val recordSpeed : Double = 0.00,
        val totalDistance : Double = 0.00,
        val totalRuns : Double = 0.00,
        val totalTime : String = "0 d 0 h 0 m 0 s",
        val level : String = "0",
        val levelDistance : Double = 0.00,
        val levelRuns : Double = 0.00,
        val started : Boolean = false,
        val stopped : Boolean = false,
        val goalSwitch : Boolean = false,
        val goalDurationSelected : Boolean = false,
        val goalHoursDefault : Int = 0,
        val goalMinutesDefault: Int = 0,
        val goalSecondsDefault : Int = 0,
        val goalDistanceDefault : Int = 0,
        val goalNotifyCheck : Boolean = false,
        val goalAutomaticFinishCheck : Boolean = false,
        val intervalSwitch : Boolean = false,
        val intervalDefault : Int = 1,
        val intervalDurationSeekbar : Float = 0.5F,
        val audioSwitch : Boolean = false,
        val runVolume : Float = 70.0F,
        val walkVolume : Float = 70.0F,
        val notificationVolume : Float = 70.0F

    ) : HomeScreenViewState()


}