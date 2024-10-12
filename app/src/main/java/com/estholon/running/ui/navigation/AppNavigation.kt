package com.estholon.running.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.estholon.running.ui.screen.authentication.RecoverScreen
import com.estholon.running.ui.screen.authentication.SignInScreen
import com.estholon.running.ui.screen.authentication.SignUpScreen
import com.estholon.running.ui.screen.home.HomeDrawer
import com.estholon.running.ui.screen.home.HomeScreen
import com.estholon.running.ui.screen.home.HomeTopBar
import com.estholon.running.ui.screen.splash.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
){

    val navController = rememberNavController()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var screen = rememberSaveable {
        mutableStateOf("")
    }

    navController.addOnDestinationChangedListener{
        controller,destination,arguments->
        screen.value = destination.route.toString()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                when (screen.value) {
                    Routes.SplashScreen.route -> {}
                    Routes.SignInScreen.route -> {}
                    Routes.SignUpScreen.route -> {}
                    Routes.RecoverScreen.route -> {}
                    else -> {
                        HomeDrawer(
                            closeDrawer = {
                                scope.launch {
                                    drawerState.apply {
                                        if(isOpen) close()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        gesturesEnabled = false
    ) {
        Scaffold(
            topBar = {
                when(screen.value){
                    Routes.SplashScreen.route -> {}
                    Routes.SignInScreen.route -> {}
                    Routes.SignUpScreen.route -> {}
                    Routes.RecoverScreen.route -> {}
                    else -> {
                        HomeTopBar(
                            showMenu = {
                                scope.launch {
                                    drawerState.apply {
                                        if(isClosed) open() else close()
                                    }
                                }
                            }
                        )
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
                        SplashScreen(
                            clearNavigation = { navController.popBackStack() },
                            navigateToHome = { navController.navigate(Routes.HomeScreen.route) },
                            navigateToSignIn = { navController.navigate(Routes.SignInScreen.route) }
                        )
                    }
                    composable(Routes.SignInScreen.route){
                        SignInScreen(
                            clearNavigation = { navController.popBackStack() },
                            navigateToHome = { navController.navigate(Routes.HomeScreen.route)},
                            navigateToSignUp = { navController.navigate(Routes.SignUpScreen.route)},
                            navigateToRecover = { navController.navigate(Routes.RecoverScreen.route)}
                        )
                    }
                    composable(Routes.SignUpScreen.route){
                        SignUpScreen(
                            clearNavigation = { navController.popBackStack() },
                            navigateToSignIn = { navController.navigate(Routes.SignInScreen.route)}
                        )
                    }
                    composable(Routes.RecoverScreen.route){
                        RecoverScreen(
                            clearNavigation = { navController.popBackStack() },
                            navigateToSignIn = { navController.navigate(Routes.SignInScreen.route)}
                        )
                    }
                    composable(Routes.HomeScreen.route){
                        HomeScreen(
                            navigateToSignIn = {
                                navController.popBackStack()
                                navController.navigate(Routes.SignInScreen.route)
                            }
                        )
                    }
                }
            }
        }
    }
}