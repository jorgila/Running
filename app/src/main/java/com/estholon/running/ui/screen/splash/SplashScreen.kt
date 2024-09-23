package com.estholon.running.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.estholon.running.R
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

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_background),
            contentDescription = "Background image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        CircularProgressIndicator(color = Color.White)
    }


}