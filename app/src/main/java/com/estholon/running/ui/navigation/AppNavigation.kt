package com.estholon.running.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.estholon.running.ui.screen.authentication.RecoverScreen
import com.estholon.running.ui.screen.authentication.SignInScreen
import com.estholon.running.ui.screen.authentication.SignUpScreen
import com.estholon.running.ui.screen.finished.FinishedScreen
import com.estholon.running.ui.screen.history.HistoryScreen
import com.estholon.running.ui.screen.history.HistoryTopBar
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
            ModalDrawerSheet(
                modifier = Modifier.fillMaxSize()
            ) {
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
                            },
                            navigateToSignIn = {
                                scope.launch {
                                    drawerState.apply {
                                        if(isOpen) close()
                                    }
                                }
                                navController.popBackStack()
                                navController.navigate(Routes.SignInScreen.route)
                            },
                            navigateToHome = {
                                scope.launch {
                                    drawerState.apply {
                                        if(isOpen) close()
                                    }
                                }
                                navController.navigate(Routes.HomeScreen.route)
                            },
                            navigateToHistory = {
                                scope.launch {
                                    drawerState.apply {
                                        if(isOpen) close()
                                    }
                                }
                                navController.navigate(Routes.HistoryScreen.route)
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
                    Routes.HistoryScreen.route -> {
                        HistoryTopBar(
                            showMenu = {
                                scope.launch {
                                    drawerState.apply {
                                        if(isClosed) open() else close()
                                    }
                                }
                            }
                        )
                    }
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
                            navigateToHome = {
                                navController.popBackStack()
                                navController.navigate(Routes.HomeScreen.route)
                            },
                            navigateToSignIn = {
                                navController.popBackStack()
                                navController.navigate(Routes.SignInScreen.route)
                            }
                        )
                    }
                    composable(Routes.SignInScreen.route){
                        SignInScreen(
                            navigateToHome = {
                                navController.popBackStack()
                                navController.navigate(Routes.HomeScreen.route)
                            },
                            navigateToSignUp = { navController.navigate(Routes.SignUpScreen.route)},
                            navigateToRecover = { navController.navigate(Routes.RecoverScreen.route)}
                        )
                    }
                    composable(Routes.SignUpScreen.route){
                        SignUpScreen(
                            navigateToSignIn = {
                                navController.popBackStack()
                                navController.navigate(Routes.SignInScreen.route)
                            }
                        )
                    }
                    composable(Routes.RecoverScreen.route){
                        RecoverScreen(
                            navigateToSignIn = {
                                navController.popBackStack()
                                navController.navigate(Routes.SignInScreen.route)
                            }
                        )
                    }
                    composable(Routes.HomeScreen.route){
                        HomeScreen(
                            navigateToFinishedScreen = {
                                navController.navigate(Routes.FinishedScreen.route)
                            }
                        )
                    }
                    composable(Routes.HistoryScreen.route){
                        HistoryScreen()
                    }
                    dialog(
                        route = Routes.FinishedScreen.route,
                        dialogProperties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            usePlatformDefaultWidth = false,
                            decorFitsSystemWindows = true
                        )
                    ){
                        // TODO
                        FinishedScreen(
                            chrono = "00:00:00",
                            dismissDialog = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}