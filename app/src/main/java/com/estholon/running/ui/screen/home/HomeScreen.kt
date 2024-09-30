package com.estholon.running.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToSignIn: () -> Unit
){

    // VARIABLES

    val isLoading = homeViewModel.isLoading.collectAsState().value


    // LAYOUT

    Button(
        onClick = {
            homeViewModel.logout()
            navigateToSignIn()
        }
    ) {
        Text("Logout")
    }

    // LOADING

    if(isLoading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }


}