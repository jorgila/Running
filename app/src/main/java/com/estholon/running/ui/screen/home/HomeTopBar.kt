package com.estholon.running.ui.screen.home

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.estholon.running.R
import com.estholon.running.ui.screen.components.TopBarComponent

@Composable
fun HomeTopBar(
    showMenu: () -> Unit
 ){
    TopBarComponent(
        topBarTitle = {
            Text(
                text = stringResource(id = R.string.app_name),
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

        }
    )
}