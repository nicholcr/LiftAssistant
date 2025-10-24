package com.example.liftassistant.ui.workout

import androidx.compose.runtime.Composable
import com.example.liftassistant.R
import com.example.liftassistant.ui.navigation.NavigationDestination

object WorkoutHistoryDestination : NavigationDestination {
    override val route = "workout_history"
    override val titleRest = R.string.workout_history_title
}

@Composable
fun WorkoutHistoryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true
) {

}