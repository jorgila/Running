package com.estholon.running.ui.screen.home


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.estholon.running.R
import com.estholon.running.ui.screen.components.BigSpinner
import com.estholon.running.ui.screen.components.Picker
import com.estholon.running.ui.screen.components.rememberPickerState
import com.estholon.running.ui.theme.Black
import com.estholon.running.ui.theme.Grey
import com.estholon.running.ui.theme.White
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import io.github.ningyuv.circularseekbar.CircularSeekbarView
import java.text.DecimalFormat


@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToFinishedScreen: () -> Unit
){

    // CONTEXTO
    val context = LocalContext.current

    // VIEW STATE
    val screenViewState = homeViewModel.homeScreenViewState.collectAsState()
    val viewState = screenViewState.value

    // VARIABLES

    val isLoading = homeViewModel.isLoading.collectAsState().value

    val currentKilometers = homeViewModel.currentKilometers.collectAsState().value
    val currentAverageSpeed = homeViewModel.currentAverageSpeed.collectAsState().value
    val currentSpeed = homeViewModel.currentSpeed.collectAsState().value

    val recordKilometers = homeViewModel.recordKilometers.collectAsState().value
    val recordAverageSpeed = homeViewModel.recordAverageSpeed.collectAsState().value
    val recordSpeed = homeViewModel.recordSpeed.collectAsState().value

    val goalKilometers = homeViewModel.goalKilometers.collectAsState().value

    val kilometersKPI = homeViewModel.kilometersKPI.collectAsState().value

    val secondsFromWatch = homeViewModel.secondsFromWatch.collectAsState().value

    val chrono : String = homeViewModel.chrono.collectAsState().value

    val intervalSwitch = homeViewModel.intervalSwitch.collectAsState().value
    val intervalDuration = homeViewModel.intervalDuration.collectAsState().value
    val rounds = homeViewModel.rounds.collectAsState().value
    val isWalkingInterval = homeViewModel.isWalkingInterval.collectAsState().value
    val runIntervalDuration = homeViewModel.runIntervalDuration.collectAsState().value
    val walkIntervalDuration = homeViewModel.walkIntervalDuration.collectAsState().value

    val runningProgress = homeViewModel.runningProgress.collectAsState().value

    //// General

    val started = homeViewModel.started.collectAsState().value
    val stopped = homeViewModel.stopped.collectAsState().value

    //// Volumes

    val notificationVolume = homeViewModel.notificationVolume.collectAsState().value
    val runVolume = homeViewModel.runVolume.collectAsState().value
    val walkVolume = homeViewModel.walkVolume.collectAsState().value

    //// MAP

    val latlng = homeViewModel.latlng.collectAsState().value
    val markerState = rememberMarkerState(position = latlng)


    val mapType = homeViewModel.mapType.collectAsState().value
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(latlng, 10f)
    }

    val coordinates = homeViewModel.coordinates.collectAsState().value

    //// Tracks

    val hardTrack = homeViewModel.hardTrack.collectAsState().value
    val hardTrackPosition = homeViewModel.hardTrackPosition.collectAsState().value
    val hardTrackRemaining = homeViewModel.hardTrackRemaining.collectAsState().value

    val softTrack = homeViewModel.softTrack.collectAsState().value
    val softTrackPosition = homeViewModel.softTrackPosition.collectAsState().value
    val softTrackRemaining = homeViewModel.softTrackRemaining.collectAsState().value

    var mapVisibility by rememberSaveable {
        mutableStateOf(false)
    }

    var intervalDurationSeekbar by rememberSaveable {
        mutableStateOf(0.5f)
    }

    val intervalMinutes = remember {
        (1..60).map {
            it.toString()
        }
    }
    val intervalMinutesPickerState = rememberPickerState()

    val formatter = DecimalFormat("00")

    val hour = remember {
        (0..24).map {
            formatter.format(it).toString()
        }
    }
    val hourPickerState = rememberPickerState()


    val minute = remember {
        (0..59).map {
            formatter.format(it).toString()
        }
    }
    val minutePickerState = rememberPickerState()

    val second = remember {
        (0..59).map {
            formatter.format(it).toString()
        }
    }
    val secondPickerState = rememberPickerState()


    val kilometers = remember{
        (0..300).map {
            it.toString()
        }
    }

    val kilometerPickerState = rememberPickerState()

    var goalSwitch by rememberSaveable {
        mutableStateOf(false)
    }

    var durationSelected by rememberSaveable {
        mutableStateOf(true)
    }

    var notifyGoalCheck by rememberSaveable {
        mutableStateOf(false)
    }

    var automaticFinishCheck by rememberSaveable {
        mutableStateOf(false)
    }

    var audioSwitch by rememberSaveable {
        mutableStateOf(false)
    }

    var averageSpeedKPI by rememberSaveable {
        mutableStateOf(0f)
    }
    var speedKPI by rememberSaveable {
        mutableStateOf(0f)
    }

    averageSpeedKPI = if(currentAverageSpeed < recordAverageSpeed){
        (currentAverageSpeed / recordAverageSpeed).toFloat()
    } else {
        1f
    }

    speedKPI = if(currentSpeed < recordSpeed){
        (currentSpeed / recordSpeed).toFloat()
    } else {
        1f
    }

    // ALERT DIALOG

    var showAlertDialog by rememberSaveable {
        mutableStateOf(
            false
        )
    }

    // PERMISSIONS

    var hasFineLocationPermission by rememberSaveable {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasCoarseLocationPermission by rememberSaveable {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher1 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        showAlertDialog = false
        if(granted) {
            hasFineLocationPermission = true
            homeViewModel.initPermissionGPS()
        } else {
            hasFineLocationPermission = false
        }
    }

    val permissionLauncher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        showAlertDialog = false
        if(granted) {
            hasCoarseLocationPermission = true
        } else {
            hasCoarseLocationPermission = false
        }
    }






    // LAYOUT

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(18.dp))
        Row {
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ){

                    CircularSeekbarView(
                        value = kilometersKPI,
                        onChange = { },
                        startAngle = -120f,
                        fullAngle =  240f,
                        dotRadius = 0.dp,
                        activeColor = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = currentKilometers.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black)
                            Text(text = "/${if(recordKilometers<goalKilometers) goalKilometers else recordKilometers}", fontSize = 10.sp)
                        }
                        Text(stringResource(R.string.distance), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    CircularSeekbarView(
                        value = averageSpeedKPI,
                        onChange = { },
                        startAngle = -120f,
                        fullAngle = 240f,
                        dotRadius = 0.dp,
                        activeColor = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = currentAverageSpeed.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(text = "/$recordAverageSpeed", fontSize = 10.sp)
                        }
                        Text(
                            stringResource(R.string.average_speed),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    CircularSeekbarView(
                        value = speedKPI,
                        onChange = { },
                        startAngle = -120f,
                        fullAngle = 240f,
                        dotRadius = 0.dp,
                        activeColor = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = currentSpeed.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(text = "/$recordSpeed", fontSize = 10.sp)
                        }
                        Text(
                            stringResource(R.string.speed),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        val intervalColor = if(isWalkingInterval) Color.Blue else Color.Red
        LinearProgressIndicator(
            progress = { runningProgress },
            modifier = Modifier.fillMaxWidth(),
            color = intervalColor
        )
        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(intervalColor)){
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chrono,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = if(intervalSwitch) TextAlign.End else TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .weight(1f)
                            .padding(18.dp)
                    )
                    if(intervalSwitch){
                        Text(
                            text = "Round $rounds",
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier
                                .weight(1f)
                                .padding(18.dp)
                        )
                    }
                }

            }
        }
        if(mapVisibility){
            Row (
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ){
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(stringResource(R.string.center).uppercase())
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {

                        homeViewModel.changeMapType(mapType != MapType.NORMAL)

                    },
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    if(mapType == MapType.NORMAL){
                        Icon(imageVector = Icons.Filled.SatelliteAlt, contentDescription = "Hybrid")
                    } else {
                        Icon(imageVector = Icons.Filled.Map, contentDescription = "Normal")
                    }
                }
            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)){

                when(viewState){
                    HomeScreenViewState.Loading -> BigSpinner()
                    is HomeScreenViewState.LatLongList -> {
                        HomeCoordinatesMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            mapType = mapType,
                            content = {
                                Polyline(coordinates)
                            },
                            viewState = viewState
                        )
                    }
                }

            }

        }
        HorizontalDivider(modifier = Modifier
            .background(Grey)
            .height(8.dp)
        )

        val text = if(mapVisibility){
            stringResource(R.string.hide)
        } else {
            stringResource(R.string.unhide)
        }

        Text(
            text = text,
            modifier = Modifier
                .background(Grey)
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .clickable { mapVisibility = !mapVisibility }
        )

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            Box(
                contentAlignment = Alignment.Center
            ){
                Button(
                    onClick = {

                        if(!stopped){
                            homeViewModel.changeStopped(true)
                            homeViewModel.stopChrono()
                        } else {
                            if(hasFineLocationPermission) {
                                if (CheckLocationServices(context)) {
                                    homeViewModel.changeLocationStatus(true)
                                    homeViewModel.changeStopped(false)
                                    homeViewModel.changeStarted(true)
                                    homeViewModel.runChrono()
                                } else {
                                    showAlertDialog = true
                                }
                            } else {
                                if(hasCoarseLocationPermission){
                                    permissionLauncher1.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                } else {
                                    permissionLauncher2.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                                }
                            }

                            //    homeViewModel.changeLocationStatus(false)
                            //    homeViewModel.changeStopped(false)
                            //    homeViewModel.changeStarted(true)
                            //    homeViewModel.runChrono()
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp)
                ) {

                    val progressButton =
                        if(!stopped) stringResource(R.string.stop) else stringResource(R.string.start)

                    Text(progressButton.uppercase())
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(top = 70.dp, start = 70.dp)
                    .height(50.dp)
                    .width(50.dp)
            ) {
                if(!stopped) {
                    IconButton(
                        onClick = { },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = White
                        ),
                        modifier = Modifier

                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_camera),
                            contentDescription = stringResource(R.string.make_photo),
                            colorFilter = ColorFilter.tint(Black)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = stringResource(R.string.settings),
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Black)
                .padding(16.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.width(300.dp),
            ) {
                Row(
                    modifier = Modifier.width(300.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.goal_settings),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = goalSwitch,
                        onCheckedChange = { goalSwitch = it },
                        enabled = if(started) false else true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedVisibility(goalSwitch){
                    Column {
                        Row {
                            Button(
                                onClick = { durationSelected = true },
                                shape = RoundedCornerShape(0),
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if(durationSelected){ MaterialTheme.colorScheme.primary } else { Color.Transparent }
                                ),
                                enabled = if(started) false else true
                            ) {
                                Text(
                                    text = stringResource(R.string.duration)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                onClick = { durationSelected = false },
                                shape = RoundedCornerShape(0),
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if(!durationSelected){ MaterialTheme.colorScheme.primary } else { Color.Transparent }
                                ),
                                enabled = if(started) false else true
                            ) {
                                Text(
                                    text = stringResource(R.string.distance)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        if(durationSelected){
                            if(!started){
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Picker(
                                        state = hourPickerState,
                                        items = hour,
                                        visibleItemsCount = 1,
                                        modifier = Modifier.weight(1f),
                                        textModifier = Modifier.padding(8.dp),
                                        textStyle = TextStyle(fontSize = 32.sp),
                                        dividerColor = Color(0xFFE8E8E8)
                                    )
                                    Text(":")
                                    Picker(
                                        state = minutePickerState,
                                        items = minute,
                                        visibleItemsCount = 1,
                                        modifier = Modifier.weight(1f),
                                        textModifier = Modifier.padding(8.dp),
                                        textStyle = TextStyle(fontSize = 32.sp),
                                        dividerColor = Color(0xFFE8E8E8)
                                    )
                                    Text(":")
                                    Picker(
                                        state = secondPickerState,
                                        items = second,
                                        visibleItemsCount = 1,
                                        modifier = Modifier.weight(1f),
                                        textModifier = Modifier.padding(8.dp),
                                        textStyle = TextStyle(fontSize = 32.sp),
                                        dividerColor = Color(0xFFE8E8E8)
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${hourPickerState.selectedItem}:${minutePickerState.selectedItem}:${secondPickerState.selectedItem}",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                                Text(" | ")
                                Text(
                                    text = "HH:MM:SS",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start
                                )
                            }
                        } else {
                            if(!started){
                                Picker(
                                    state = kilometerPickerState,
                                    items = kilometers,
                                    visibleItemsCount = 1,
                                    textModifier = Modifier.padding(8.dp),
                                    textStyle = TextStyle(fontSize = 32.sp),
                                    dividerColor = Color(0xFFE8E8E8)
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${kilometerPickerState.selectedItem}",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                                Text(" | ")
                                Text(
                                    text = "KM",
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        homeViewModel.getSecondsFromWatch(
                            "${if(hourPickerState.selectedItem.isNullOrEmpty()) "00" else hourPickerState.selectedItem}:" +
                                    "${if(minutePickerState.selectedItem.isNullOrEmpty()) "00" else minutePickerState.selectedItem}:" +
                                    "${if(secondPickerState.selectedItem.isNullOrEmpty()) "00" else secondPickerState.selectedItem}"
                        )

                        Spacer(modifier = Modifier.height(18.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = notifyGoalCheck,
                                onCheckedChange = { notifyGoalCheck = it },
                                enabled = if(started) false else true
                            )
                            Text(
                                text = stringResource(R.string.notify_goal_when_finish)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = automaticFinishCheck,
                                onCheckedChange = { automaticFinishCheck = it },
                                enabled = if(started) false else true
                            )
                            Text(
                                text = stringResource(R.string.automatic_run_finish)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.interval_running),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = intervalSwitch,
                        onCheckedChange = { homeViewModel.changeIntervalSwitch() },
                        enabled = if(started) false else true,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(intervalSwitch){
                    Column {
                        Text(
                            stringResource(R.string.intervals_duration),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if(!started){
                            Picker(
                                state = intervalMinutesPickerState,
                                items = intervalMinutes,
                                visibleItemsCount = 1,
                                textModifier = Modifier.padding(8.dp),
                                textStyle = TextStyle(fontSize = 32.sp),
                                dividerColor = Color(0xFFE8E8E8)
                            )
                        }
                        if(!intervalMinutesPickerState.selectedItem.isNullOrEmpty()){
                            homeViewModel.changeIntervalDuration(intervalMinutesPickerState.selectedItem.toString().toLong())
                        }
                        Text(
                            text = "The interval will last ${intervalMinutesPickerState.selectedItem} Minute/s",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if(!intervalMinutesPickerState.selectedItem.isNullOrEmpty()){
                            homeViewModel.getRunIntervalDuration(
                                intervalMinutesPickerState.selectedItem.toLong(),
                                intervalDurationSeekbar
                            )
                            homeViewModel.getWalkIntervalDuration(
                                intervalMinutesPickerState.selectedItem.toLong(),
                                intervalDurationSeekbar
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(R.string.run_walk_distribution))
                        Box(
                        ){
                            CircularSeekbarView(
                                value = intervalDurationSeekbar,
                                onChange = { if(!started){intervalDurationSeekbar = it}},
                                startAngle = -90f,
                                fullAngle = 180f,
                                steps =
                                if(!intervalMinutesPickerState.selectedItem.isNullOrEmpty()){
                                    if(intervalMinutesPickerState.selectedItem.toInt()>10){
                                        if(intervalMinutesPickerState.selectedItem.toInt()>30){
                                            intervalMinutesPickerState.selectedItem.toInt() * 60 / 300
                                        } else {
                                            intervalMinutesPickerState.selectedItem.toInt() * 60 / 60
                                        }
                                    } else {
                                        intervalMinutesPickerState.selectedItem.toInt() * 60 / 15
                                    }

                                } else {
                                    1
                                },
                                lineWeight = 5.dp,
                                activeColor = if(started) Color.DarkGray else MaterialTheme.colorScheme.primary,
                                dotColor = if(started) Color.DarkGray else MaterialTheme.colorScheme.primary
                            )
                            Box(
                                modifier = Modifier.padding(top = 180.dp)
                            ){
                                Column {
                                    Text("Run:")
                                    Text("$runIntervalDuration")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Walk:")
                                    Text("$walkIntervalDuration")
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.width(300.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.audio_settings),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = audioSwitch,
                        onCheckedChange = { audioSwitch = it },
                    )
                }
                AnimatedVisibility(audioSwitch){
                    Column(

                    ) {
                        Text(
                            text = stringResource(R.string.audio_settings_for_run)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = runVolume,
                            onValueChange = { homeViewModel.changeRunVolume(it) },
                            valueRange = 0f..100f
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AnimatedVisibility(intervalSwitch){
                            Column {
                                Text(
                                    text = stringResource(R.string.audio_settings_for_walk)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Slider(
                                    value = walkVolume,
                                    onValueChange = { homeViewModel.changeWalkVolume(it) },
                                    valueRange = 0f..100f
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        Text(
                            text = stringResource(R.string.audio_settings_for_notifications)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = notificationVolume,
                            onValueChange = { homeViewModel.changeNotificationVolume(it) },
                            valueRange = 0f..100f
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Slider(
                    value = hardTrack,
                    onValueChange = { homeViewModel.changePositionHardTrack(it) },
                    valueRange = 0f .. 100f
                )
                Row {
                    Text("$hardTrackPosition")
                    Spacer(modifier = Modifier.weight(1f))
                    Text("-$hardTrackRemaining")
                }
                Spacer(modifier = Modifier.height(8.dp))
                if(intervalSwitch){
                    Slider(
                        value = softTrack,
                        onValueChange = { homeViewModel.changePositionSoftTrack(it)},
                        valueRange = 0f .. 100f
                    )
                    Row {
                        Text("$softTrackPosition")
                        Spacer(modifier = Modifier.weight(1f))
                        Text("-$softTrackRemaining")
                    }
                }
            }
        }

        Box(
            modifier = Modifier.height(100.dp)
        ){

        }
    }


    if(chrono!="00:00:00"){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd){
            FloatingActionButton(
                onClick = {
                    navigateToFinishedScreen()
                    homeViewModel.stopChrono()
                    homeViewModel.resetChrono()
                    homeViewModel.changeStopped(true)
                    homeViewModel.changeStarted(false)
                    homeViewModel.changeLocationStatus(false)

                },
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    text = stringResource(R.string.finish)
                )
            }
        }
    }

    // LOADING

    if(isLoading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }

    if(showAlertDialog){
        AlertDialog(
            title = {
                Text("Allow access to location")
            },
            text = {
                Text("If you want to obtain run data like distance or speed, it is necessary to activate the gps")
            },
            onDismissRequest = {
                showAlertDialog = false
            },
            confirmButton = {
                TextButton (
                    onClick = {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }
                ){
                    Text("Activate")
                }
            },
            dismissButton = {
                TextButton (
                    onClick = {
                        homeViewModel.changeLocationStatus(false)
                        homeViewModel.changeStopped(false)
                        homeViewModel.changeStarted(true)
                        homeViewModel.runChrono()
                        showAlertDialog = false
                    }
                ){
                    Text("Ignore")
                }
            }
        )
    }

}


fun isLocationGranted(context: Context) : Boolean {

    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    && ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

}

fun CheckLocationServices(context: Context) : Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}


