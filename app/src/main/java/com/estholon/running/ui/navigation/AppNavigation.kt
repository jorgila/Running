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
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.estholon.running.ui.screen.authentication.RecoverScreen
import com.estholon.running.ui.screen.authentication.SignInScreen
import com.estholon.running.ui.screen.authentication.SignUpScreen
import com.estholon.running.ui.screen.camera.CameraScreen
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
                            navigateToFinishedScreen = { chrono,durationGoal,intervalDuration,runIntervalDuration,walkIntervalDuration,distance, distanceGoal, minAltitude, maxAltitude, avgSpeed, maxSpeed, runId ->
                                navController.navigate("${Routes.FinishedScreen.route}/$chrono/$durationGoal/$intervalDuration/$runIntervalDuration/$walkIntervalDuration/$distance/$distanceGoal/$minAltitude/$maxAltitude/$avgSpeed/$maxSpeed/$runId")
                            }
                        )
                    }
                    composable(Routes.HistoryScreen.route){
                        HistoryScreen()
                    }
                    composable(
                        route = "${Routes.CameraScreen.route}/{runId}"
                    ){ backStackEntry ->

                        val runId = backStackEntry.arguments?.getString("runId") ?: ""

                        CameraScreen(
                            runId = runId
                        )
                    }
                    dialog(
                        route = "${Routes.FinishedScreen.route}/{chrono}/{durationGoal}/{intervalDuration}/{runIntervalDuration}/{walkIntervalDuration}/{distance}/{distanceGoal}/{minAltitude}/{maxAltitude}/{avgSpeed}/{maxSpeed}/{runId}",
                        dialogProperties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            usePlatformDefaultWidth = false,
                            decorFitsSystemWindows = true
                        )
                    ){ backStackEntry ->

                        val runId = backStackEntry.arguments?.getString("runId")
                        val passedChrono = backStackEntry.arguments?.getString("chrono") ?: "00:00:00"
                        val durationGoal = backStackEntry.arguments?.getString("goalDuration") ?: "00:00:00"
                        val intervalDuration = backStackEntry.arguments?.getString("intervalDuration") ?: "0"
                        val runIntervalDuration = backStackEntry.arguments?.getString("runIntervalDuration") ?: "00:00"
                        val walkIntervalDuration = backStackEntry.arguments?.getString("walkIntervalDuration") ?: "00:00"
                        val distance = backStackEntry.arguments?.getString("distance") ?: "0.00"
                        val distanceGoal = backStackEntry.arguments?.getString("distanceGoal") ?: "0"
                        val minAltitude = backStackEntry.arguments?.getString("minAltitude") ?: "0"
                        val maxAltitude = backStackEntry.arguments?.getString("maxAltitude") ?: "0"
                        val avgSpeed = backStackEntry.arguments?.getString("avgSpeed") ?: "0.0"
                        val maxSpeed = backStackEntry.arguments?.getString("maxSpeed") ?: "0.0"

                        FinishedScreen(
                            runId = runId,
                            chrono = passedChrono,
                            durationGoal = durationGoal,
                            intervalDuration = intervalDuration,
                            runIntervalDuration = runIntervalDuration,
                            walkIntervalDuration = walkIntervalDuration,
                            distance = distance,
                            distanceGoal = distanceGoal,
                            minAltitude = minAltitude,
                            maxAltitude = maxAltitude,
                            avgSpeed = avgSpeed,
                            maxSpeed = maxSpeed,
                            dismissDialog = { navController.popBackStack() },
                            navigateToCameraScreen = { runId ->
                                navController.navigate("${Routes.CameraScreen.route}/$runId")
                            }
                        )
                    }
                }
            }
        }
    }
}