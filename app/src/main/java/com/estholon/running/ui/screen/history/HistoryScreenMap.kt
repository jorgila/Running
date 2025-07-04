package com.estholon.running.ui.screen.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun HistoryScreenMap(
    modifier: Modifier,
    coordinates: List<LatLng>,
    cameraPositionState: CameraPositionState,
    mapType: MapType,
    content: @Composable () -> Unit = {},
    eventFlow: Flow<HistoryScreenEvent>
){

    var isMapLoaded by rememberSaveable {
        mutableStateOf(false)
    }

    // Create properties with mapType
    val properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = mapType
            )
        )
    }

    // Create scope
    val scope = rememberCoroutineScope()

    // Calcular bounds para esta carrera específica
    val boundingBox = remember(coordinates) {
        if (coordinates.isNotEmpty()) {
            LatLngBounds.Builder().apply {
                coordinates.forEach { include(it) }
            }.build()
        } else null
    }

    // Centrar el mapa en las coordenadas de esta carrera específica
    LaunchedEffect(boundingBox) {
        boundingBox?.let { bounds ->
            scope.launch {
                try {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngBounds(bounds, 64),
                        durationMs = 1000
                    )
                } catch (e: Exception) {
                    // Fallback si hay error con bounds
                    if (coordinates.isNotEmpty()) {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(coordinates.first(), 15f),
                            durationMs = 1000
                        )
                    }
                }
            }
        }
    }

    // Google Map View

    Box(
        modifier = Modifier.fillMaxSize()
    ){

        GoogleMap(
            modifier = modifier,
            properties = properties,
            cameraPositionState = cameraPositionState,
            onMapLoaded = { isMapLoaded = true }
        ){
            content()
        }

        if(!isMapLoaded){

            AnimatedVisibility(
                modifier = Modifier.matchParentSize(),
                visible = !isMapLoaded,
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    CircularProgressIndicator(
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    )
                }
            }
        }

    }





}

fun zoomAll(
    scope: CoroutineScope,
    cameraPositionState: CameraPositionState,
    boundingBox: LatLngBounds
) {
    scope.launch {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(boundingBox, 64),
            durationMs = 1000
        )
    }
}
