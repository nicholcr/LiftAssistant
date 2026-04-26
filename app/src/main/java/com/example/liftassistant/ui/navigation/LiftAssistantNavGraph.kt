@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.liftassistant.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.liftassistant.R
import com.example.liftassistant.data.Workout
import com.example.liftassistant.ui.AppViewModelProvider
import com.example.liftassistant.ui.exercise.AddExerciseDestination
import com.example.liftassistant.ui.exercise.AddExerciseScreen
import com.example.liftassistant.ui.exercise.EditExerciseDestination
import com.example.liftassistant.ui.exercise.EditExerciseScreen
import com.example.liftassistant.ui.exercise.ExerciseListDestination
import com.example.liftassistant.ui.exercise.ExerciseListScreen
import com.example.liftassistant.ui.home.HomeDestination
import com.example.liftassistant.ui.home.HomeScreen
import com.example.liftassistant.ui.home.HomeViewModel
import com.example.liftassistant.ui.workout.PerformWorkoutDestination
import com.example.liftassistant.ui.workout.PerformWorkoutScreen
import com.example.liftassistant.ui.workout.WorkoutSummaryDestination
import com.example.liftassistant.ui.workout.WorkoutSummaryScreen
import com.example.liftassistant.ui.workout_routine.WorkoutRoutineListDestination
import com.example.liftassistant.ui.workout_routine.WorkoutRoutineListScreen

@Composable
fun LiftAssistantNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val homeUiState by homeViewModel.homeUiState.collectAsState()

    Scaffold(
        bottomBar = {
            Column {
                ActiveWorkoutBanner(
                    inProgressWorkout = homeUiState.inProgressWorkout,
                    onBannerClick = {
                        homeUiState.inProgressWorkout?.let { workout ->
                            navController.navigate(
                                "${PerformWorkoutDestination.route}/${workout.id}"
                            )
                        }
                    }
                )
                LiftAssistantBottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.History.route,
            modifier = modifier.padding(innerPadding)
        ) {
            navigation(
                route = BottomNavItem.History.route,
                startDestination = HomeDestination.route
            ) {
                composable(route = HomeDestination.route) {
                    HomeScreen(
                        navigateToStartWorkout = {
                            navController.navigate(PerformWorkoutDestination.route)
                        },
                        navigateToWorkoutSummary = { workoutId ->
                            navController.navigate(
                                "${WorkoutSummaryDestination.route}/$workoutId"
                            )
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
            }

            navigation(
                route = BottomNavItem.Exercises.route,
                startDestination = ExerciseListDestination.route
            ) {
                composable(route = ExerciseListDestination.route) {
                    ExerciseListScreen(
                        navigateToAddExercise = {
                            navController.navigate(AddExerciseDestination.route)
                        },
                        navigateToEditExercise = { exerciseId ->
                            navController.navigate(
                                "${EditExerciseDestination.route}/$exerciseId"
                            )
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
                composable(
                    route = EditExerciseDestination.routeWithArgs,
                    arguments = listOf(navArgument(EditExerciseDestination.exerciseIdArg) {
                        type = NavType.IntType
                    })
                ) {
                    EditExerciseScreen(
                        navigateBack = { navController.popBackStack() },
                        onNavigateUp = { navController.navigateUp() }
                    )
                }
            }

            navigation(
                route = BottomNavItem.Routines.route,
                startDestination = WorkoutRoutineListDestination.route
            ) {
                composable(route = WorkoutRoutineListDestination.route) {
                    WorkoutRoutineListScreen(
                        onNavigateUp = { navController.navigateUp() }
                    )
                }
            }

            composable(route = PerformWorkoutDestination.route) {
                PerformWorkoutScreen(
                    navigateBack = { navController.popBackStack() },
                    navigateToWorkoutSummary = { workoutId ->
                        navController.navigate(
                            "${WorkoutSummaryDestination.route}/$workoutId"
                        ) {
                            popUpTo(PerformWorkoutDestination.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = PerformWorkoutDestination.routeWithArgs,
                arguments = listOf(navArgument(PerformWorkoutDestination.workoutIdArg) {
                    type = NavType.IntType
                })
            ) {
                PerformWorkoutScreen(
                    navigateBack = { navController.popBackStack() },
                    navigateToWorkoutSummary = { workoutId ->
                        navController.navigate(
                            "${WorkoutSummaryDestination.route}/$workoutId"
                        ) {
                            popUpTo(PerformWorkoutDestination.routeWithArgs) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LiftAssistantBottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.titleRes)
                    )
                },
                label = { Text(stringResource(item.titleRes)) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
private fun ActiveWorkoutBanner(
    inProgressWorkout: Workout?,
    onBannerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = inProgressWorkout != null,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column(modifier = modifier) {
            HorizontalDivider()
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBannerClick() }
                        .padding(
                            horizontal = dimensionResource(R.dimen.padding_medium),
                            vertical = 12.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = inProgressWorkout?.name ?: "",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = stringResource(R.string.workout_in_progress),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(R.string.resume_workout),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}