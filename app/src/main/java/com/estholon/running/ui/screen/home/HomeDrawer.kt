package com.estholon.running.ui.screen.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.estholon.running.R

@Composable
fun HomeDrawer(
    closeDrawer: () -> Unit
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