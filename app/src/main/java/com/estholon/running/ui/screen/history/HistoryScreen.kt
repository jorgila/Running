package com.estholon.running.ui.screen.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.estholon.running.R
import com.estholon.running.ui.theme.White

@Composable
fun HistoryScreen(

){

    val runs = listOf(1,2,3,4,5)

    Column {
        LazyColumn() {
            items(runs){ run ->
                RunItem(run = run)
            }
        }
    }

}

@Composable
fun RunItem(run: Int){

    val chrono = "00:10:00"
    val durationGoal = "00:15:00"
    val intervalDuration = "10"
    val runIntervalDuration = "00:05:00"
    val walkIntervalDuration = "00:05:00"
    val distance = "10.0"
    val distanceGoal = "15.0"
    val minAltitude = "0.0"
    val maxAltitude = "0.0"
    val avgSpeed = "10.0"
    val maxSpeed = "30.0"


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
                    text = "00/00/0000",
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = chrono,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = distance,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = avgSpeed,
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
                        text = chrono,
                        fontSize = 20.sp
                    )
                    Text("Goal")
                    Text(durationGoal)
                    Text("Intervals")
                    Text("$intervalDuration ($runIntervalDuration / $walkIntervalDuration)")
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(White)
                        .padding(8.dp)
                ) {
                    Text(stringResource(R.string.distance))
                    Text(
                        text = "$distance Km",
                        fontSize = 20.sp
                    )
                    Text("Goal")
                    Text("$distanceGoal.00 Km")
                    Text("Slope")
                    Text("min: $minAltitude / max: $maxAltitude")
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(White)
                        .padding(8.dp)
                ){
                    Text("Average Speed")
                    Text(
                        text = "$avgSpeed Km/H",
                        fontSize = 20.sp
                    )
                    Text("Maximum Speed")
                    Text(
                        text = "$maxSpeed Km/H",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}