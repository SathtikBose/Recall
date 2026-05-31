package com.buildstack.recall.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.buildstack.recall.presentation.home.HomeScreen
import com.buildstack.recall.presentation.splash.SplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Home : Screen("home_screen")
    object AddEdit : Screen("add_edit_screen")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToAdd = {
                    navController.navigate(Screen.AddEdit.route)
                }
            )
        }
        composable(route = Screen.AddEdit.route) {
            // AddEditReminderScreen()
        }
    }
}
