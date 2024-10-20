package com.estholon.running.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.estholon.running.R
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

    Column {
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