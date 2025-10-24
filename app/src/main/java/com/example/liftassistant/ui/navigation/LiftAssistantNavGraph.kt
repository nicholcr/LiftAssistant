package com.example.liftassistant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.liftassistant.ui.home.HomeDestination
import com.example.liftassistant.ui.home.HomeScreen
import com.example.liftassistant.ui.workout.WorkoutHistoryScreen
import com.example.liftassistant.ui.workout.WorkoutHistoryDestination

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun LiftAssistantNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToWorkoutHistory = { navController.navigate(WorkoutHistoryDestination.route) }
            )
        }
        composable(route = WorkoutHistoryDestination.route) {
            WorkoutHistoryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}