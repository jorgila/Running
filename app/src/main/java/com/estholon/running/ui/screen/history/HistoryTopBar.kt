package com.estholon.running.ui.screen.history

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.estholon.running.R
import com.estholon.running.ui.screen.components.TopBarComponent
import kotlin.math.exp

@Composable
fun HistoryTopBar(
    showMenu: () -> Unit
) {

    var showDropdownMenu by rememberSaveable {
        mutableStateOf(false)
    }

    var date by rememberSaveable {
        mutableStateOf(false)
    }

    var duration by rememberSaveable {
        mutableStateOf(false)
    }

    var distance by rememberSaveable {
        mutableStateOf(false)
    }

    var averageSpeed by rememberSaveable {
        mutableStateOf(false)
    }

    var speed by rememberSaveable {
        mutableStateOf(false)
    }

    TopBarComponent(
        topBarTitle = {
            Text(
                text = stringResource(id = R.string.history),
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
        },
        topBarNavigationIcon = {
            IconButton(
                onClick = {
                    showMenu() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.menu)
                )
            }
        },
        topBarActions = {
            IconButton(
                onClick = {
                    showDropdownMenu = !showDropdownMenu
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.options)
                )
                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = !showDropdownMenu }
                ) {
                    DropdownMenuItem(
                        text = {
                            if(date){
                                Text(stringResource(R.string.date_oldest_first))
                            } else {
                                Text(stringResource(R.string.date_most_recent_first))
                            }
                        },
                        onClick = {
                            date = !date
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            if(duration){
                                Text(stringResource(R.string.duration_shortest_first))
                            } else {
                                Text(stringResource(R.string.duration_longest_first))
                            }
                        },
                        onClick = {
                            duration = !duration
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            if(distance){
                                Text(stringResource(R.string.distance_shortest_first))
                            } else {
                                Text(stringResource(R.string.distance_longest_first))
                            }
                        },
                        onClick = { distance = !distance }
                    )
                    DropdownMenuItem(
                        text = {
                            if(averageSpeed){
                                Text(stringResource(R.string.average_speed_slowest_first))
                            } else {
                                Text(stringResource(R.string.average_speed_fastest_first))
                            }
                        },
                        onClick = { averageSpeed = !averageSpeed }
                    )
                    DropdownMenuItem(
                        text = {
                            if(speed){
                                Text(stringResource(R.string.speed_slowest_first))
                            } else {
                                Text(stringResource(R.string.speed_fastest_first))
                            }
                        },
                        onClick = { speed = !speed }
                    )
                }
            }
        }
    )

}