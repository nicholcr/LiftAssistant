@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.liftassistant.ui.workout_routine

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftassistant.LiftAssistantTopAppBar
import com.example.liftassistant.R
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.ui.AppViewModelProvider
import com.example.liftassistant.ui.navigation.NavigationDestination
import com.example.liftassistant.ui.theme.LiftAssistantTheme
import kotlinx.coroutines.launch

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
    modifier: Modifier = Modifier,
    viewModel: EditWorkoutRoutineViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    val availableExercises by viewModel.availableExercises.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LiftAssistantTopAppBar(
                title = stringResource(EditWorkoutRoutineDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::addSlot) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_slot)
                )
            }
        }
    ) { innerPadding ->
        RoutineForm(
            formState = viewModel.formState,
            availableExercises = availableExercises,
            onNameChange = viewModel::updateName,
            onSlotUpdate = viewModel::updateSlot,
            onSlotRemove = viewModel::removeSlot,
            onSlotReorder = viewModel::reorderSlots,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveRoutine()
                    navigateBack()
                }
            },
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditWorkoutRoutineScreenPreview() {
    LiftAssistantTheme {
        RoutineForm(
            formState = RoutineFormState(
                name = "Upper A",
                slots = listOf(
                    RoutineSlotFormState(
                        order = 0,
                        setScheme = "2x5, 1x5+",
                        slotType = SlotType.Fixed,
                        fixedExercise = Exercise(id = 1, name = "Bench Press"),
                        isValid = true
                    ),
                    RoutineSlotFormState(
                        order = 1,
                        setScheme = "2x5, 1x5+",
                        slotType = SlotType.Fixed,
                        fixedExercise = Exercise(id = 2, name = "Barbell Row"),
                        isValid = true
                    ),
                    RoutineSlotFormState(
                        order = 2,
                        setScheme = "3x8-12",
                        slotType = SlotType.Flexible,
                        categoryLabel = "Push accessory",
                        note = "Prioritize push before pull",
                        isValid = true
                    ),
                    RoutineSlotFormState(
                        order = 3,
                        setScheme = "3x8-12",
                        slotType = SlotType.Flexible,
                        categoryLabel = "Pull accessory",
                        isValid = true
                    )
                ),
                isValid = true
            ),
            availableExercises = emptyList(),
            onNameChange = {},
            onSlotUpdate = { _, _ -> },
            onSlotRemove = {},
            onSlotReorder = { _, _ -> },
            onSaveClick = {},
            contentPadding = PaddingValues(0.dp)
        )
    }
}