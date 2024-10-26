package com.estholon.running.ui.screen.finished

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.estholon.running.R
import com.estholon.running.ui.navigation.Routes

@Composable
fun FinishedScreen(
    finishedViewModel: FinishedViewModel = hiltViewModel()
){

    val level = finishedViewModel.level.collectAsState().value
    val totalRunning = finishedViewModel.totalTime.collectAsState().value
    val currentKilometers = finishedViewModel.currentKilometers.collectAsState().value
    val totalKilometers = finishedViewModel.totalKilometers.collectAsState().value
    val currentRuns = finishedViewModel.currentRuns.collectAsState().value
    val totalRuns = finishedViewModel.totalRuns.collectAsState().value


    Box(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.primary)
    ){
        Row {
            Column(modifier = Modifier
                .width(117.dp)
            ) {
                Text(
                    text = level,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = NavigationDrawerItemDefaults.ItemPadding.calculateStartPadding(
                                LayoutDirection.Ltr
                            )
                        )
                        .background(MaterialTheme.colorScheme.secondary)
                )
                Image(
                    painter = painterResource(R.drawable.img_running),
                    contentDescription = stringResource(R.string.running),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .padding(
                            start = NavigationDrawerItemDefaults.ItemPadding.calculateStartPadding(
                                LayoutDirection.Ltr
                            )
                        )
                        .background(Color.Black)
                )
                Text(
                    text = totalRunning,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(
                            start = NavigationDrawerItemDefaults.ItemPadding.calculateStartPadding(
                                LayoutDirection.Ltr
                            ),
                            bottom = 24.dp
                        )
                )
            }
            Column(modifier = Modifier
                .weight(1f)
                .padding(
                    start = NavigationDrawerItemDefaults.ItemPadding.calculateStartPadding(
                        LayoutDirection.Ltr
                    )
                )
            ) {
                Text(
                    text = stringResource(R.string.distance),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Text(
                    "$currentKilometers / $totalKilometers km"
                )
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                Text(
                    text = stringResource(R.string.runs),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Text(
                    "$currentRuns / $totalRuns runs"
                )
            }
        }
    }
}