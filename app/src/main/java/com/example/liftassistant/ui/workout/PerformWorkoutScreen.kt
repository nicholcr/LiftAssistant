@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.liftassistant.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftassistant.LiftAssistantTopAppBar
import com.example.liftassistant.R
import com.example.liftassistant.data.ExerciseWithCategories
import com.example.liftassistant.ui.AppViewModelProvider
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
    modifier: Modifier = Modifier,
    viewModel: PerformWorkoutViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState by viewModel.uiState.collectAsState()
    var showEndWorkoutDialog by remember { mutableStateOf(false) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            uiState.workout?.id?.let { navigateToWorkoutSummary(it) }
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LiftAssistantTopAppBar(
                title = uiState.workout?.name ?: stringResource(PerformWorkoutDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RestTimerFab()
                AddExerciseFab(onClick = { showAddExerciseDialog = true })
            }
        }
    ) { innerPadding ->
        if (uiState.workout == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            PerformWorkoutContent(
                uiState = uiState,
                onEndWorkoutClick = { showEndWorkoutDialog = true },
                onAddSet = viewModel::addSet,
                onUpdateSet = viewModel::updateSet,
                onDeleteSet = viewModel::deleteSet,
                onReorder = viewModel::reorderExercises,
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if (showEndWorkoutDialog) {
        EndWorkoutDialog(
            isSaving = uiState.isSaving,
            onConfirm = {
                viewModel.endWorkout()
            },
            onDismiss = { showEndWorkoutDialog = false }
        )
    }

    if (showAddExerciseDialog) {
        AddExerciseDialog(
            exerciseList = uiState.availableExercises,
            onExerciseSelected = { exercise ->
                viewModel.addExercise(exercise)
                showAddExerciseDialog = false
            },
            onQuickAdd = { name ->
                viewModel.quickAddExercise(name)
                showAddExerciseDialog = false
            },
            onDismiss = { showAddExerciseDialog = false }
        )
    }
}

@Composable
private fun PerformWorkoutContent(
    uiState: PerformWorkoutUiState,
    onEndWorkoutClick: () -> Unit,
    onAddSet: (Int) -> Unit,
    onUpdateSet: (com.example.liftassistant.data.WorkoutSet) -> Unit,
    onDeleteSet: (com.example.liftassistant.data.WorkoutSet) -> Unit,
    onReorder: (Int, Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Button(
            onClick = onEndWorkoutClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_medium),
                    vertical = dimensionResource(R.dimen.padding_small)
                )
        ) {
            Text(stringResource(R.string.end_workout))
        }
        LazyColumn(
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_small)
            ),
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.padding_small)
            )
        ) {
            items(
                items = uiState.exerciseItems,
                key = { it.workoutExercise.id }
            ) { exerciseItem ->
                WorkoutExerciseCard(
                    exerciseItem = exerciseItem,
                    onAddSet = { onAddSet(exerciseItem.workoutExercise.id) },
                    onUpdateSet = onUpdateSet,
                    onDeleteSet = onDeleteSet,
                    onMoveUp = {
                        val index = uiState.exerciseItems.indexOf(exerciseItem)
                        if (index > 0) onReorder(index, index - 1)
                    },
                    onMoveDown = {
                        val index = uiState.exerciseItems.indexOf(exerciseItem)
                        if (index < uiState.exerciseItems.size - 1) onReorder(index, index + 1)
                    }
                )
            }
        }
    }
}

@Composable
private fun EndWorkoutDialog(
    isSaving: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text(stringResource(R.string.end_workout)) },
        text = { Text(stringResource(R.string.end_workout_confirmation)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator()
                } else {
                    Text(stringResource(R.string.confirm))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun AddExerciseDialog(
    exerciseList: List<ExerciseWithCategories>,
    onExerciseSelected: (ExerciseWithCategories) -> Unit,
    onQuickAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredExercises = remember(exerciseList, searchQuery) {
        if (searchQuery.isBlank()) exerciseList
        else exerciseList.filter {
            it.exercise.name.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_exercise)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                )
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(stringResource(R.string.search_exercises)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(
                        items = filteredExercises,
                        key = { it.exercise.id }
                    ) { exerciseWithCategories ->
                        TextButton(
                            onClick = { onExerciseSelected(exerciseWithCategories) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = exerciseWithCategories.exercise.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (exerciseWithCategories.categories.isNotEmpty()) {
                                    Text(
                                        text = exerciseWithCategories.categories
                                            .joinToString(", ") { it.name },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                }
                HorizontalDivider()
                TextButton(
                    onClick = {
                        onQuickAdd(
                            searchQuery.ifBlank {
                                stringResource(R.string.new_exercise)
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Text(
                        text = if (searchQuery.isBlank())
                            stringResource(R.string.create_and_add_new_exercise)
                        else
                            stringResource(R.string.create_and_add_exercise, searchQuery),
                        modifier = Modifier.padding(
                            start = dimensionResource(R.dimen.padding_small)
                        )
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}