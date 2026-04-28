@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.liftassistant.ui.workout_routine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.liftassistant.data.WorkoutRoutine
import com.example.liftassistant.ui.AppViewModelProvider
import com.example.liftassistant.ui.navigation.NavigationDestination
import com.example.liftassistant.ui.theme.LiftAssistantTheme

object WorkoutRoutineListDestination : NavigationDestination {
    override val route = "workout_routine_list"
    override val titleRes = R.string.workout_routine_list_title
}

@Composable
fun WorkoutRoutineListScreen(
    navigateToAddWorkoutRoutine: () -> Unit,
    navigateToWorkoutRoutine: (Int) -> Unit,
    navigateToEditWorkoutRoutine: (Int) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WorkoutRoutineListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState by viewModel.uiState.collectAsState()
    var routineToDelete by remember { mutableStateOf<WorkoutRoutine?>(null) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LiftAssistantTopAppBar(
                title = stringResource(WorkoutRoutineListDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToAddWorkoutRoutine) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_routine)
                )
            }
        }
    ) { innerPadding ->
        WorkoutRoutineListBody(
            routineList = uiState.workoutRoutineList,
            onRoutineClick = navigateToWorkoutRoutine,
            onEditClick = navigateToEditWorkoutRoutine,
            onDeleteClick = { routineToDelete = it },
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }

    routineToDelete?.let { routine ->
        DeleteRoutineDialog(
            routineName = routine.name,
            onConfirm = {
                viewModel.deleteWorkoutRoutine(routine)
                routineToDelete = null
            },
            onDismiss = { routineToDelete = null }
        )
    }
}

@Composable
private fun WorkoutRoutineListBody(
    routineList: List<WorkoutRoutine>,
    onRoutineClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (WorkoutRoutine) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (routineList.isEmpty()) {
            Text(
                text = stringResource(R.string.empty_routine_list),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding)
            )
        } else {
            LazyColumn(
                contentPadding = contentPadding,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.padding_small)
                )
            ) {
                items(
                    items = routineList,
                    key = { it.id }
                ) { routine ->
                    WorkoutRoutineCard(
                        routine = routine,
                        onRoutineClick = { onRoutineClick(routine.id) },
                        onEditClick = { onEditClick(routine.id) },
                        onDeleteClick = { onDeleteClick(routine) },
                        modifier = Modifier.padding(
                            dimensionResource(R.dimen.padding_small)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutRoutineCard(
    routine: WorkoutRoutine,
    onRoutineClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRoutineClick() }
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = routine.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_routine)
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_routine)
                )
            }
        }
    }
}

@Composable
private fun DeleteRoutineDialog(
    routineName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_routine)) },
        text = {
            Text(stringResource(R.string.delete_routine_confirmation, routineName))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun WorkoutRoutineListEmptyPreview() {
    LiftAssistantTheme {
        WorkoutRoutineListBody(
            routineList = emptyList(),
            onRoutineClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutRoutineListPopulatedPreview() {
    LiftAssistantTheme {
        WorkoutRoutineListBody(
            routineList = listOf(
                WorkoutRoutine(id = 1, name = "Upper A"),
                WorkoutRoutine(id = 2, name = "Upper B"),
                WorkoutRoutine(id = 3, name = "Legs A"),
                WorkoutRoutine(id = 4, name = "Legs B")
            ),
            onRoutineClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}