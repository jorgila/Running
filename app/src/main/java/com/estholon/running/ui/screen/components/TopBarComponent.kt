package com.estholon.running.ui.screen.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComponent(
    topBarTitle: @Composable () -> Unit,
    topBarNavigationIcon: @Composable () -> Unit,
    topBarActions: @Composable () -> Unit
){

    CenterAlignedTopAppBar(
        title = { topBarTitle() },
        navigationIcon = { topBarNavigationIcon() },
        actions = { topBarActions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}