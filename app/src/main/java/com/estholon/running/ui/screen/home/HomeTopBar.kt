package com.estholon.running.ui.screen.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.estholon.running.R
import com.estholon.running.ui.screen.components.TopBarComponent

@Composable
fun HomeTopBar(

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

        },
        topBarActions = {

        }
    )
}