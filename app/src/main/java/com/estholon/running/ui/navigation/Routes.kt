package com.estholon.running.ui.navigation

sealed class Routes (val route: String){

    data object SplashScreen : Routes("splashScreen")
    data object SignInScreen : Routes("signInScreen")
    data object SignUpScreen : Routes("signUpScreen")
    data object RecoverScreen : Routes("recoverScreen")
    data object HomeScreen : Routes("homeScreen")
    data object HistoryScreen : Routes("historyScreen")
    data object FinishedScreen : Routes("finishedScreen")

}