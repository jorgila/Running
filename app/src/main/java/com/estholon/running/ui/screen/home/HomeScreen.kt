package com.estholon.running.ui.screen.home


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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.estholon.running.R
import com.estholon.running.ui.screen.components.Picker
import com.estholon.running.ui.screen.components.rememberPickerState
import com.estholon.running.ui.theme.Black
import com.estholon.running.ui.theme.White
import com.google.maps.android.compose.GoogleMap
import io.github.ningyuv.circularseekbar.CircularSeekbarView
import java.text.DecimalFormat

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToSignIn: () -> Unit
){

    // VARIABLES

    val isLoading = homeViewModel.isLoading.collectAsState().value

    val currentKilometers = homeViewModel.currentKilometers.collectAsState().value

    val recordKilometers = homeViewModel.recordKilometers.collectAsState().value

    val currentAverageSpeed = homeViewModel.currentAverageSpeed.collectAsState().value

    val recordAverageSpeed = homeViewModel.recordAverageSpeed.collectAsState().value

    val currentSpeed = homeViewModel.currentSpeed.collectAsState().value

    val recordSpeed = homeViewModel.recordSpeed.collectAsState().value

    var mapVisibility by rememberSaveable {
        mutableStateOf(true)
    }

    var intervalSwitch by rememberSaveable {
        mutableStateOf(true)
    }

    var intervalDurationSeekbar by rememberSaveable {
        mutableStateOf(0f)
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
        (0..1000).map {
            it.toString()
        }
    }

    val kilometerPickerState = rememberPickerState()

    var goalSwitch by rememberSaveable {
        mutableStateOf(true)
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
        mutableStateOf(true)
    }

    var kilometersKPI by rememberSaveable {
        mutableStateOf(0f)
    }
    var averageSpeedKPI by rememberSaveable {
        mutableStateOf(0f)
    }
    var speedKPI by rememberSaveable {
        mutableStateOf(0f)
    }

    kilometersKPI = if(currentKilometers < recordKilometers){
        (currentKilometers / recordKilometers).toFloat()
    } else {
        1f
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
                            Text(text = "/$recordKilometers", fontSize = 10.sp)
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
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)){
            Text(
                text = "00:00:00",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            )
        }
        if(mapVisibility==true){
            Box(
                contentAlignment = Alignment.BottomStart,
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
            }

            HomeGoogleMaps()

        }
        HorizontalDivider(modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
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
                .background(MaterialTheme.colorScheme.primary)
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
                    onClick = { },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp)
                ) {
                    Text(stringResource(R.string.start).uppercase())
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(top = 70.dp,start=70.dp)
            ){
                IconButton(
                    onClick = { },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = White
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp)
                ){
                    Image(
                        painter = painterResource(R.drawable.ic_camera),
                        contentDescription = stringResource(R.string.make_photo),
                        colorFilter = ColorFilter.tint(Black)
                    )
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
            Column(
                modifier = Modifier.width(300.dp),
            ) {
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
                        onCheckedChange = { intervalSwitch = it },
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if(intervalSwitch){
                    Text(stringResource(R.string.intervals_duration))
                    Spacer(modifier = Modifier.height(8.dp))
                    Picker(
                        state = intervalMinutesPickerState,
                        items = intervalMinutes,
                        visibleItemsCount = 1,
                        textModifier = Modifier.padding(8.dp),
                        textStyle = TextStyle(fontSize = 32.sp),
                        dividerColor = Color(0xFFE8E8E8)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.run_walk_distribution))
                    Box(
                    ){
                        CircularSeekbarView(
                            value = intervalDurationSeekbar,
                            onChange = { intervalDurationSeekbar = it},
                            startAngle = -90f,
                            fullAngle = 180f,
                            lineWeight = 5.dp,
                            activeColor = MaterialTheme.colorScheme.primary
                        )
                        Box(
                            modifier = Modifier.padding(top = 180.dp)
                        ){
                            Column {
                                Text("Run:")
                                Text("00:00")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Walk:")
                                Text("00:00")
                            }
                        }
                    }
                }
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
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if(goalSwitch){
                    Row {
                        Button(
                            onClick = { durationSelected = true },
                            shape = RoundedCornerShape(0),
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if(durationSelected){ MaterialTheme.colorScheme.primary } else { Color.Transparent }
                            )
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
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.distance)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    if(durationSelected){
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
                    } else {
                        Picker(
                            state = kilometerPickerState,
                            items = kilometers,
                            visibleItemsCount = 1,
                            textModifier = Modifier.padding(8.dp),
                            textStyle = TextStyle(fontSize = 32.sp),
                            dividerColor = Color(0xFFE8E8E8)
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = notifyGoalCheck,
                            onCheckedChange = { notifyGoalCheck = it }
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
                            onCheckedChange = { automaticFinishCheck = it }
                        )
                        Text(
                            text = stringResource(R.string.automatic_run_finish)
                        )
                        Spacer(modifier = Modifier.weight(1f))
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
            }
        }

        Box(
            modifier = Modifier.height(100.dp)
        ){

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


}

@Composable
fun HomeGoogleMaps(){
    GoogleMap(modifier = Modifier
        .fillMaxWidth()
        .height(300.dp))
}