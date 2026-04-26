package com.example.liftassistant.ui.workout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.liftassistant.R
import com.example.liftassistant.ui.navigation.NavigationDestination

object PerformWorkoutDestination : NavigationDestination {
    override val route = "perform_workout"
    override val titleRes = R.string.perform_workout_title
    const val workoutIdArg = "workoutId"
    val routeWithArgs = "$route/{$workoutIdArg}"
}

@Composable
fun PerformWorkoutScreen(
    navigateBack: () -> Unit,
    navigateToWorkoutSummary: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

}