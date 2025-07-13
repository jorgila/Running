package com.estholon.running.ui.screen.history

import android.net.Uri
import com.estholon.running.domain.model.LocationModel
import com.estholon.running.domain.model.RunModel
import com.estholon.running.ui.screen.home.HomeScreenViewState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.MapType

sealed class HistoryScreenViewState {

    data object Loading : HistoryScreenViewState()

    data class LatLongList(
        val coordinates: List<LatLng>,
        val boundingBox: LatLngBounds
    ) : HistoryScreenViewState()


    data class HistoryUIState(
        val isLoading : Boolean = false,
        val message: Boolean = false,
        val kpiTotalDistance : Double = 0.00,
        val kpiTotalRuns : Double = 0.00,
        val kpiTotalTime : String = "0 d 0 h 0 m 0 s",
        val runs: List<RunModel> = emptyList(),
        val mapType: MapType = MapType.NORMAL,
        val mapLatLongTarget : LatLng = LatLng(0.0,0.0),
        val images : List<String> = emptyList()
    ) : HistoryScreenViewState()


}