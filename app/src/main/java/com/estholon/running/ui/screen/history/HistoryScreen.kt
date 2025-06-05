package com.estholon.running.ui.screen.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.estholon.running.R
import com.estholon.running.domain.model.RunModel
import com.estholon.running.ui.theme.White

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = hiltViewModel()
){

    val state by historyViewModel.historyUIState.collectAsState()

    Column {
        LazyColumn() {
            items(state.runs){ run ->
                RunItem(
                    run = run,
                    onDeleteSelected = { runId, runDistance, runDuration ->
                        historyViewModel.deleteRunAndLinkedData(runId, runDistance, runDuration)
                    }
                )
            }
        }
    }

}

@Composable
fun RunItem(
    run: RunModel,
    onDeleteSelected: (String, Double, String) -> Unit
){

    var showDetail by rememberSaveable { mutableStateOf(false) }
    val showDetailIcon = if(showDetail) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown

    Column {

        Card(

        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.background(Color.Black)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = run.startDate?.replace(Regex("(\\d{4})/(\\d{2})/(\\d{2})"), "$3/$2/$1") ?: "00/00/0000",
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = run.kpiDuration,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = run.kpiDistance.toString(),
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = run.kpiAvgSpeed.toString(),
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = showDetailIcon,
                    contentDescription = "Show detail",
                    modifier = Modifier.clickable {
                        showDetail = !showDetail
                    },
                    tint = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        if(showDetail){
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(White)
                        .padding(8.dp)
                ) {
                    Text(stringResource(R.string.duration))
                    Text(
                        text = run.kpiDuration,
                        fontSize = 20.sp
                    )
                    Text("Goal")
                    Text("${run.goalHoursDefault}:${run.goalMinutesDefault}:${run.goalSecondsDefault}")
                    Text("Intervals")
                    Text("${run.intervalDefault} (${run.intervalRunDuration} / ${run.intervalWalkDuration})")
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(White)
                        .padding(8.dp)
                ) {
                    Text(stringResource(R.string.distance))
                    Text(
                        text = "${run.kpiDistance} Km",
                        fontSize = 20.sp
                    )
                    Text("Goal")
                    Text("${run.goalDistanceDefault}.00 Km")
                    Text("Slope")
                    Text("min: ${run.kpiMinAltitude} / max: ${run.kpiMaxAltitude}")
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(White)
                        .padding(8.dp)
                ){
                    Text("Average Speed")
                    Text(
                        text = "${run.kpiAvgSpeed} Km/H",
                        fontSize = 20.sp
                    )
                    Text("Maximum Speed")
                    Text(
                        text = "${run.kpiMaxSpeed} Km/H",
                        fontSize = 20.sp
                    )
                }
            }
            Row {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        onDeleteSelected(run.runId, run.kpiDistance, run.kpiDuration)
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}