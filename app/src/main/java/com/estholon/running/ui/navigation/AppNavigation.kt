package com.estholon.running.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
){

    val navController = rememberNavController()

    var screen = rememberSaveable {
        mutableStateOf("")
    }

    navController.addOnDestinationChangedListener{
        controller,destination,arguments->
        screen.value = destination.route.toString()
    }

    Scaffold(
        topBar = {
            when(screen.value){
                Routes.SplashScreen.route -> {}
                Routes.SignInScreen.route -> {}
                Routes.SignUpScreen.route -> {}
                Routes.RecoverScreen.route -> {}
                else -> {

                }
            }
        },
        bottomBar = {
            when(screen.value){
                Routes.SplashScreen.route -> {}
                Routes.SignInScreen.route -> {}
                Routes.SignUpScreen.route -> {}
                Routes.RecoverScreen.route -> {}
                else -> {

                }
            }
        }
    ) {paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)){
            NavHost(
                navController = navController,
                startDestination = Routes.SplashScreen.route,
                modifier = modifier
            ){
                composable(Routes.SplashScreen.route){

                }
                composable(Routes.SignInScreen.route){

                }
                composable(Routes.SignUpScreen.route){

                }
                composable(Routes.RecoverScreen.route){

                }
                composable(Routes.HomeScreen.route){

                }
            }
        }
    }

}