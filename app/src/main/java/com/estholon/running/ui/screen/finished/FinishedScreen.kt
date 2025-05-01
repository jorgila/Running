package com.estholon.running.ui.screen.finished

import android.util.Log
import android.widget.Space
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.estholon.running.R
import com.estholon.running.ui.navigation.Routes
import com.estholon.running.ui.theme.White

@Composable
fun FinishedScreen(
    runId: String?,
    chrono: String,
    durationGoal: String,
    intervalDuration: String,
    runIntervalDuration: String,
    walkIntervalDuration: String,
    distance: String,
    distanceGoal: String,
    minAltitude: String,
    maxAltitude: String,
    avgSpeed: String,
    maxSpeed: String,
    dismissDialog: () -> Unit,
    finishedViewModel: FinishedViewModel = hiltViewModel()
){
    // Contexto
    val context = LocalContext.current

    // UI State
    val finishedUIState = finishedViewModel.finishedUIState.collectAsState().value

    if(runId==null){
        Toast.makeText(context, stringResource(R.string.error_in_run_creation),Toast.LENGTH_LONG).show()
    } else {
        finishedViewModel.initRun(runId)
        Column(
            modifier = Modifier
                .width(400.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Row {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = dismissDialog
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", modifier = Modifier.padding(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Column(modifier = Modifier
                    .width(117.dp)
                ) {
                    Text(
                        text = "Level ${finishedUIState.kpiLevel}",
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
                        text = finishedUIState.kpiTotalTime,
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
                        "${finishedUIState.kpiTotalDistance} / ${finishedUIState.kpiLevelDistance} km"
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
                        "${finishedUIState.kpiTotalRuns} / ${finishedUIState.kpiLevelRuns} runs"
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(White)
                        .padding(8.dp)
                ) {
                    Text(stringResource(R.string.duration))
                    Text(
                        text = chrono,
                        fontSize = 32.sp
                    )
                    Text("Goal")
                    Text(durationGoal)
                    Text("Intervals")
                    Text("$intervalDuration ($runIntervalDuration / $walkIntervalDuration)")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .background(White)
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(stringResource(R.string.distance))
                        Text(
                            text = "$distance Km",
                            fontSize = 32.sp
                        )
                        Text("Goal")
                        Text("$distanceGoal.00 Km")
                        Text("Slope")
                        Text("min: $minAltitude / max: $maxAltitude")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .background(White)
                            .fillMaxWidth()
                            .padding(8.dp)
                    ){
                        Text("Average Speed")
                        Text(
                            text = "$avgSpeed Km/H",
                            fontSize = 32.sp
                        )
                        Text("Maximum Speed")
                        Text(
                            text = "$maxSpeed Km/H",
                            fontSize = 32.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary)
                .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { finishedViewModel.deleteRunAndLinkedData(id = runId) },
                    colors = ButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(text = stringResource(R.string.delete))
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {},
                    colors = ButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.padding(20.dp)
                ) {
                    Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = "Camera")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {},
                    colors = ButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.padding(20.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = "Share")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

    if(finishedUIState.message!=null){
        if(finishedUIState.message){
            Toast.makeText(context, stringResource(R.string.deleted_successfully),Toast.LENGTH_LONG).show()
            dismissDialog()
        } else {
            Toast.makeText(context,
                stringResource(R.string.an_error_has_happen_while_deleting_the_run),Toast.LENGTH_LONG).show()
        }
    }

}