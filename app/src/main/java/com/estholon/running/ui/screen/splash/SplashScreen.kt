package com.estholon.running.ui.screen.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel = hiltViewModel(),
    clearNavigation: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToSignIn: () -> Unit
){

    // LOGIC

    LaunchedEffect(key1 = true) {
        delay(1000)
        clearNavigation()
        if(splashViewModel.isUserLogged()){
            navigateToHome()
        } else {
            navigateToSignIn()
        }
    }


    // LAYOUT




}