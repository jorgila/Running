package com.estholon.running.ui.screen.home

import android.gesture.OrientedBoundingBox
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

sealed class HomeScreenViewState {

    data object Loading : HomeScreenViewState()

    data class LatLongList(
        val coordinates: List<LatLng>,
        val boundingBox: LatLngBounds
    ) : HomeScreenViewState()

}