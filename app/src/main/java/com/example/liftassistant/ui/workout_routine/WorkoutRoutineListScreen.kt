package com.example.liftassistant.ui.workout_routine

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.liftassistant.ui.navigation.NavigationDestination

object WorkoutRoutineListDestination : NavigationDestination {
    override val route = "workout_routine_list"
    override val titleRes = R.string.workout_routine_list_title
}
@Composable
fun WorkoutRoutineListScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {

}