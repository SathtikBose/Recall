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
    object AddEdit : Screen("add_edit_screen?reminderId={id}") {
        fun createRoute(id: Int) = "add_edit_screen?reminderId=$id"
    }
    object Settings : Screen("settings_screen")
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
                },
                onEditReminder = { id ->
                    navController.navigate(Screen.AddEdit.createRoute(id))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(
            route = Screen.AddEdit.route,
            arguments = listOf(
                androidx.navigation.navArgument("id") {
                    type = androidx.navigation.NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            com.buildstack.recall.presentation.add.AddEditReminderScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = Screen.Settings.route) {
            com.buildstack.recall.presentation.settings.SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
