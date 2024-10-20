package com.estholon.running.ui.screen.home

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
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
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

    val email = homeViewModel.user.collectAsState().value
    val level = homeViewModel.level.collectAsState().value
    val totalRunning = homeViewModel.totalTime.collectAsState().value
    val currentKilometers = homeViewModel.currentKilometers.collectAsState().value
    val totalKilometers = homeViewModel.totalKilometers.collectAsState().value
    val currentRuns = homeViewModel.currentRuns.collectAsState().value
    val totalRuns = homeViewModel.totalRuns.collectAsState().value

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
            text = email,
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
            /* TODO */
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