package com.estholon.running.ui.screen.home

import android.graphics.Paint.Align
import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.estholon.running.R
import com.estholon.running.ui.theme.Black
import com.estholon.running.ui.theme.Grey
import com.estholon.running.ui.theme.White
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.compose.GoogleMap
import io.github.ningyuv.circularseekbar.CircularSeekbarView

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