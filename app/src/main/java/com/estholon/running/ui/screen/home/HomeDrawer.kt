package com.estholon.running.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

    Box(
        modifier = Modifier.fillMaxWidth(),
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
    Text("Account", modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
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