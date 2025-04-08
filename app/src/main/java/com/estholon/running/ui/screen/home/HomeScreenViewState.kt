package com.estholon.running.ui.screen.home

import android.gesture.OrientedBoundingBox
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import javax.inject.Inject

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
        val totalTime : Double = 0.00

    ) : HomeScreenViewState()

}