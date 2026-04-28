@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.liftassistant.ui.workout_routine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftassistant.LiftAssistantTopAppBar
import com.example.liftassistant.R
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.RoutineSlot
import com.example.liftassistant.data.WorkoutRoutine
import com.example.liftassistant.ui.AppViewModelProvider
import com.example.liftassistant.ui.navigation.NavigationDestination
import com.example.liftassistant.ui.theme.LiftAssistantTheme

object WorkoutRoutineDestination : NavigationDestination {
    override val route = "workout_routine"
    override val titleRes = R.string.workout_routine_title
    const val routineIdArg = "routineId"
    val routeWithArgs = "$route/{$routineIdArg}"
}

@Composable
fun WorkoutRoutineScreen(
    navigateToEditWorkoutRoutine: (Int) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WorkoutRoutineViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LiftAssistantTopAppBar(
                title = uiState.routine?.name
                    ?: stringResource(WorkoutRoutineDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateUp,
                actions = {
                    IconButton(
                        onClick = {
                            uiState.routine?.let {
                                navigateToEditWorkoutRoutine(it.id)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_routine)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            WorkoutRoutineContent(
                uiState = uiState,
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun WorkoutRoutineContent(
    uiState: WorkoutRoutineUiState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    if (uiState.slotItems.isEmpty()) {
        Box(
            modifier = modifier.padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.empty_routine_slots),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            )
        }
    } else {
        LazyColumn(
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_small)
            ),
            modifier = modifier.padding(
                horizontal = dimensionResource(R.dimen.padding_small)
            )
        ) {
            items(
                items = uiState.slotItems,
                key = { it.slot.id }
            ) { slotItem ->
                RoutineSlotCard(slotItem = slotItem)
            }
        }
    }
}

@Composable
private fun RoutineSlotCard(
    slotItem: RoutineSlotItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (slotItem.fixedExercise != null)
                        slotItem.fixedExercise.name
                    else
                        slotItem.slot.categoryLabel
                            ?: stringResource(R.string.flexible_slot),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = slotItem.slot.setScheme,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (slotItem.fixedExercise == null) {
                Text(
                    text = stringResource(R.string.flexible_slot_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            slotItem.slot.note?.let { note ->
                HorizontalDivider()
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutRoutineContentPreview() {
    LiftAssistantTheme {
        WorkoutRoutineContent(
            uiState = WorkoutRoutineUiState(
                routine = WorkoutRoutine(id = 1, name = "Upper A"),
                slotItems = listOf(
                    RoutineSlotItem(
                        slot = RoutineSlot(
                            id = 1,
                            routineId = 1,
                            order = 0,
                            setScheme = "2x5, 1x5+",
                            fixedExerciseId = 1
                        ),
                        fixedExercise = Exercise(
                            id = 1,
                            name = "Bench Press"
                        )
                    ),
                    RoutineSlotItem(
                        slot = RoutineSlot(
                            id = 2,
                            routineId = 1,
                            order = 1,
                            setScheme = "2x5, 1x5+",
                            fixedExerciseId = 2
                        ),
                        fixedExercise = Exercise(
                            id = 2,
                            name = "Barbell Row"
                        )
                    ),
                    RoutineSlotItem(
                        slot = RoutineSlot(
                            id = 3,
                            routineId = 1,
                            order = 2,
                            setScheme = "3x8-12",
                            categoryLabel = "Push accessory",
                            note = "Prioritize push before pull"
                        ),
                        fixedExercise = null
                    ),
                    RoutineSlotItem(
                        slot = RoutineSlot(
                            id = 4,
                            routineId = 1,
                            order = 3,
                            setScheme = "3x8-12",
                            categoryLabel = "Pull accessory"
                        ),
                        fixedExercise = null
                    )
                ),
                isLoading = false
            ),
            contentPadding = PaddingValues(0.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutRoutineContentEmptyPreview() {
    LiftAssistantTheme {
        WorkoutRoutineContent(
            uiState = WorkoutRoutineUiState(
                routine = WorkoutRoutine(id = 1, name = "Upper A"),
                slotItems = emptyList(),
                isLoading = false
            ),
            contentPadding = PaddingValues(0.dp)
        )
    }
}