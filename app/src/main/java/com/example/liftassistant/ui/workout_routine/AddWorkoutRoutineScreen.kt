package com.example.liftassistant.ui.workout_routine

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.liftassistant.R
import com.example.liftassistant.ui.navigation.NavigationDestination

object AddWorkoutRoutineDestination : NavigationDestination {
    override val route = "add_workout_routine"
    override val titleRes = R.string.add_routine
}

@Composable
fun AddWorkoutRoutineScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {

}