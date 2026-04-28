package com.example.liftassistant.ui.workout_routine

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.liftassistant.R
import com.example.liftassistant.ui.navigation.NavigationDestination

object EditWorkoutRoutineDestination : NavigationDestination {
    override val route = "edit_workout_routine"
    override val titleRes = R.string.edit_routine
    const val routineIdArg = "routineId"
    val routeWithArgs = "$route/{$routineIdArg}"
}

@Composable
fun EditWorkoutRoutineScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {

}