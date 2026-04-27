@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.liftassistant.ui.workout

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
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
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.Workout
import com.example.liftassistant.data.WorkoutExercise
import com.example.liftassistant.data.WorkoutSet
import com.example.liftassistant.ui.AppViewModelProvider
import com.example.liftassistant.ui.navigation.NavigationDestination
import com.example.liftassistant.ui.theme.LiftAssistantTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    modifier: Modifier = Modifier,
    viewModel: WorkoutSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LiftAssistantTopAppBar(
                title = uiState.workout?.name
                    ?: stringResource(WorkoutSummaryDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateUp,
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_workout)
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
            WorkoutSummaryContent(
                uiState = uiState,
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if (showDeleteDialog) {
        DeleteWorkoutDialog(
            workoutName = uiState.workout?.name ?: "",
            onConfirm = {
                viewModel.deleteWorkout { navigateBack() }
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun WorkoutSummaryContent(
    uiState: WorkoutSummaryUiState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        modifier = modifier.padding(horizontal = dimensionResource(R.dimen.padding_small))
    ) {
        item {
            WorkoutSummaryHeader(workout = uiState.workout)
        }
        items(
            items = uiState.exerciseItems,
            key = { it.workoutExercise.id }
        ) { exerciseItem ->
            WorkoutSummaryExerciseCard(exerciseItem = exerciseItem)
        }
        if (uiState.exerciseItems.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_exercises_logged),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
        }
    }
}

@Composable
private fun WorkoutSummaryHeader(
    workout: Workout?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            workout?.date?.let { date ->
                SummaryDetailRow(
                    label = stringResource(R.string.workout_date),
                    value = formatDate(date)
                )
            }
            workout?.duration?.let { duration ->
                SummaryDetailRow(
                    label = stringResource(R.string.workout_duration),
                    value = formatDuration(duration)
                )
            }
        }
    }
}

@Composable
private fun WorkoutSummaryExerciseCard(
    exerciseItem: WorkoutSummaryExerciseItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Text(
                text = exerciseItem.exercise.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            )
            HorizontalDivider()
            if (exerciseItem.sets.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_sets_logged),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
                )
            } else {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.padding_medium)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.set_header),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = stringResource(R.string.weight_header),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = stringResource(R.string.reps_header),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    exerciseItem.sets.forEachIndexed { index, set ->
                        SummarySetRow(
                            setNumber = index + 1,
                            workoutSet = set
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummarySetRow(
    setNumber: Int,
    workoutSet: WorkoutSet,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.padding_medium)
        )
    ) {
        Text(
            text = if (workoutSet.isAmrap) "$setNumber+" else "$setNumber",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (workoutSet.weight > 0f)
                stringResource(R.string.weight_value, workoutSet.weight)
            else
                stringResource(R.string.not_logged),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (workoutSet.reps > 0)
                workoutSet.reps.toString()
            else
                stringResource(R.string.not_logged),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DeleteWorkoutDialog(
    workoutName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_workout)) },
        text = {
            Text(stringResource(R.string.delete_workout_confirmation, workoutName))
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

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("EEE, MMM d yyyy", Locale.getDefault())
    return formatter.format(date)
}

private fun formatDuration(durationMillis: Long): String {
    val totalMinutes = durationMillis / 1000 / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}

@Preview(showBackground = true)
@Composable
private fun WorkoutSummaryScreenPreview() {
    LiftAssistantTheme {
        WorkoutSummaryContent(
            uiState = WorkoutSummaryUiState(
                workout = Workout(
                    id = 1,
                    name = "Upper A — Mon Apr 7",
                    date = Date(),
                    endTime = Date(Date().time + 3_600_000L)
                ),
                exerciseItems = listOf(
                    WorkoutSummaryExerciseItem(
                        workoutExercise = WorkoutExercise(
                            id = 1,
                            workoutId = 1,
                            exerciseId = 1,
                            order = 0
                        ),
                        exercise = Exercise(
                            id = 1,
                            name = "Bench Press",
                            prWeight = 185f,
                            latestWeight = 175f
                        ),
                        sets = listOf(
                            WorkoutSet(
                                id = 1,
                                workoutExerciseId = 1,
                                order = 0,
                                reps = 5,
                                weight = 175f
                            ),
                            WorkoutSet(
                                id = 2,
                                workoutExerciseId = 1,
                                order = 1,
                                reps = 5,
                                weight = 175f
                            ),
                            WorkoutSet(
                                id = 3,
                                workoutExerciseId = 1,
                                order = 2,
                                reps = 8,
                                weight = 175f,
                                isAmrap = true
                            )
                        )
                    ),
                    WorkoutSummaryExerciseItem(
                        workoutExercise = WorkoutExercise(
                            id = 2,
                            workoutId = 1,
                            exerciseId = 2,
                            order = 1
                        ),
                        exercise = Exercise(
                            id = 2,
                            name = "Barbell Row",
                            prWeight = 155f,
                            latestWeight = 145f
                        ),
                        sets = listOf(
                            WorkoutSet(
                                id = 4,
                                workoutExerciseId = 2,
                                order = 0,
                                reps = 5,
                                weight = 145f
                            )
                        )
                    )
                ),
                isLoading = false
            ),
            contentPadding = PaddingValues(0.dp)
        )
    }
}