package com.example.liftassistant.ui.workout

import androidx.compose.runtime.Composable
import com.example.liftassistant.R
import com.example.liftassistant.ui.navigation.NavigationDestination

object WorkoutSummaryDestination : NavigationDestination {
    override val route = "workout_summary"
    override val titleRes = R.string.workout_summary_title
    const val workoutIdArg = "workoutId"
    val routeWithArgs = "$route/{$workoutIdArg}"
}

@Composable
fun WorkoutSummaryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true
) {

}