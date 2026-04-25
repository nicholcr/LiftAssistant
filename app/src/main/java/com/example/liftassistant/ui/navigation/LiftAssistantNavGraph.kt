package com.example.liftassistant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.liftassistant.ui.exercise.AddExerciseDestination
import com.example.liftassistant.ui.exercise.AddExerciseScreen
import com.example.liftassistant.ui.exercise.ExerciseListDestination
import com.example.liftassistant.ui.exercise.ExerciseListScreen
import com.example.liftassistant.ui.home.HomeDestination
import com.example.liftassistant.ui.home.HomeScreen
import com.example.liftassistant.ui.workout.WorkoutSummaryDestination
import com.example.liftassistant.ui.workout.WorkoutSummaryScreen

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
                navigateToWorkoutSummary = {
                    navController.navigate("${WorkoutSummaryDestination.route}/$it")
                }
            )
        }

        composable(
            route = WorkoutSummaryDestination.routeWithArgs,
            arguments = listOf(navArgument(WorkoutSummaryDestination.workoutIdArg) {
                type = NavType.IntType
            })
        ) {
            WorkoutSummaryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(route = ExerciseListDestination.route) {
            ExerciseListScreen(
                navigateToAddExercise = {
                    navController.navigate(AddExerciseDestination.route)
                },
                navigateToEditExercise = {
                    // TODO: navigate to EditExerciseScreen once built
                },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(route = AddExerciseDestination.route) {
            AddExerciseScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}