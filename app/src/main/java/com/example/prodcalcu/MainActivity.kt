package com.example.prodcalcu

import ResultScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prodcalcu.ui.calc_filter.CalculatorScreen
import com.example.prodcalcu.ui.auth.LoginScreen

import androidx.navigation.navArgument
import com.example.prodcalcu.logic.CampType
import com.example.prodcalcu.logic.Climate
import com.example.prodcalcu.ui.auth.IntroScreen
import com.example.prodcalcu.ui.profile.CalculationsListScreen
import com.example.prodcalcu.ui.profile.ProfileScreen
import com.example.prodcalcu.ui.register.RegistrationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            Modifier.safeDrawingPadding()
            val navController: NavHostController = rememberNavController()
            NavHost(navController = navController, startDestination = "intro") {

                composable("intro") {
                    IntroScreen(navController)
                }
                composable("login") {
                    LoginScreen(navController)
                }
                composable("register") {
                    RegistrationScreen(navController)
                }
                composable("calculator") {
                    CalculatorScreen(navController)
                }
                composable(
                    "result?calcName={calcName}&dayNumber={dayNumber}&personNumber={personNumber}&activity={activity}&season={season}",
                    arguments = listOf(
                        navArgument("calcName") { type = NavType.StringType },
                        navArgument("dayNumber") { type = NavType.IntType },
                        navArgument("personNumber") { type = NavType.IntType },
                        navArgument("activity") { type = NavType.StringType },
                        navArgument("season") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val calcName = backStackEntry.arguments?.getString("calcName") ?: ""
                    val dayNumber = backStackEntry.arguments?.getInt("dayNumber") ?: 1
                    val personNumber = backStackEntry.arguments?.getInt("personNumber") ?: 1
                    val activity = backStackEntry.arguments?.getString("activity")?.let { CampType.valueOf(it) } ?: CampType.PERMANENT_CAMP
                    val season = backStackEntry.arguments?.getString("season")?.let { Climate.valueOf(it) } ?: Climate.SUMMER

                    ResultScreen(navController, calcName, dayNumber, personNumber, activity, season)
                }
                composable("profile") {
                    ProfileScreen(navController)
                }
                composable("list") {
                    CalculationsListScreen(navController)
                }

            }
        }
    }

}
