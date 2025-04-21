package com.estholon.running.ui.screen.home

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.MapType

sealed class HomeScreenViewState {

    data object Loading : HomeScreenViewState()

    data class LatLongList(
        val coordinates: List<LatLng>,
        val boundingBox: LatLngBounds
    ) : HomeScreenViewState()

    data class HomeUIState(
        val mapIsLoading: Boolean = false,
        val user : String = "Anonimous",
        val kpiDistanceCircularSeekbarValue: Float = 0F,
        val kpiDistance : Double = 0.0,
        val kpiRecordDistance : Double = 0.0,
        val kpiAvgSpeedCircularSeekbarValue: Float = 0F,
        val kpiAvgSpeed : Double = 0.0,
        val kpiRecordAvgSpeed : Double = 0.0,
        val kpiSpeedCircularSeekbarValue: Float = 0F,
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
        var rounds : Int = 1,
        val started : Boolean = false,
        val stopped : Boolean = false,
        val mapType: MapType = MapType.NORMAL,
        val mapLatLongTarget : LatLng = LatLng(0.0,0.0),
        val goalSwitch : Boolean = false,
        val goalDurationSelected : Boolean = false,
        val goalHoursDefault : Int = 0,
        val goalMinutesDefault: Int = 0,
        val goalSecondsDefault : Int = 0,
        val goalDistanceDefault : Int = 0,
        val goalDistance : Double = 0.0,
        val goalNotifyCheck : Boolean = false,
        val goalAutomaticFinishCheck : Boolean = false,
        val intervalSwitch : Boolean = false,
        val intervalDefault : Int = 1,
        val intervalDurationSeekbar : Float = 0.5F,
        val intervalRunDuration : String = "00:00",
        val intervalWalkDuration : String = "00:00",
        val audioSwitch : Boolean = false,
        val audioRunVolume : Float = 70.0F,
        val audioWalkVolume : Float = 70.0F,
        val audioNotificationVolume : Float = 70.0F,
        val audioRunTrack : Float = 0F,
        val audioRunTrackPosition : String = "00:00:00",
        val audioRunRemainingTrackPosition : String = "00:00:00",
        val audioWalkTrack : Float = 0F,
        val audioWalkTrackPosition : String = "00:00:00",
        val audioWalkRemainingTrackPosition : String = "00:00:00",

        ) : HomeScreenViewState()


}