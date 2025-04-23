package com.estholon.running.ui.screen.home

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

@Composable
fun HomeDrawer(
    closeDrawer: () -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToHistory: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current


   val homeUIState = homeViewModel.homeUIState.collectAsState().value

    var showResetPreferences by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(
            onClick = { closeDrawer() }
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.close)
            )
        }
    }
    Column(modifier = Modifier.verticalScroll(rememberScrollState())){
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
        ){
            Row {
                Column(modifier = Modifier
                    .width(117.dp)
                ) {
                    Text(
                        text = "Level ${homeUIState.kpiLevel}",
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
                        text = homeUIState.kpiTotalTime,
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
                        "${homeUIState.kpiTotalDistance} / ${homeUIState.kpiLevelDistance} km"
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
                        "${homeUIState.kpiTotalRuns} / ${homeUIState.kpiLevelRuns} runs"
                    )
                }
            }
        }


        Box(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(
                start = NavigationDrawerItemDefaults.ItemPadding.calculateStartPadding(
                    LayoutDirection.Ltr
                ), bottom = 24.dp
            )
        ){
            Text(
                text = homeUIState.user ?: "Anonimous",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.running)) },
            selected = false,
            onClick = {
                navigateToHome()
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                    contentDescription = stringResource(R.string.running)
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.history)) },
            selected = false,
            onClick = {
                navigateToHistory()
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = stringResource(R.string.history)
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.account), modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.reset_preferences)) },
            selected = false,
            onClick = {
                showResetPreferences = true
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.reset_preferences)
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.logout)) },
            selected = false,
            onClick = {
                homeViewModel.logout()
                navigateToSignIn()
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = stringResource(R.string.logout)
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }

    if(showResetPreferences){
        AlertDialog(
            title = {
                Text(text = stringResource(R.string.reset_preferences))
            },
            text = {
                Text(text = stringResource(R.string.if_you_continue_you_can_reset_the_preferences_saved_from_your_last_run))
            },
            onDismissRequest = {
                showResetPreferences = false
            },
            confirmButton = {
                TextButton (
                    onClick = {
                        var result : Boolean = false
                        result = homeViewModel.resetPreferences()
                        if(result){
                            Toast.makeText(context,
                                context.getString(R.string.reset_has_been_successful),Toast.LENGTH_LONG).show()
                        }
                        showResetPreferences = false
                    }
                ) {
                    Text(text= stringResource(R.string.reset))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            dismissButton = {
                TextButton(
                    onClick = {
                        showResetPreferences = false
                    }
                ) {
                    Text(text= stringResource(R.string.cancel))
                }
            },
        )
    }
}

