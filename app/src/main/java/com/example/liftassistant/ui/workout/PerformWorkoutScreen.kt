@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.liftassistant.ui.workout

import android.Manifest
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftassistant.LiftAssistantTopAppBar
import com.example.liftassistant.R
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.ExerciseWithCategories
import com.example.liftassistant.data.WorkoutExercise
import com.example.liftassistant.data.WorkoutSet
import com.example.liftassistant.ui.AppViewModelProvider
import com.example.liftassistant.ui.navigation.NavigationDestination
import com.example.liftassistant.ui.theme.LiftAssistantTheme
import kotlinx.coroutines.delay
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.Date

object PerformWorkoutDestination : NavigationDestination {
    override val route = "perform_workout"
    override val titleRes = R.string.perform_workout_title
    const val workoutIdArg = "workoutId"
    const val routineIdArg = "routineId"
    val routeWithArgs = "$route/{$workoutIdArg}"
    val routeWithRoutineArg = "$route?routineId={$routineIdArg}"
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
    var elapsedSeconds by remember { mutableStateOf(0) }
    var showEndWorkoutDialog by remember { mutableStateOf(false) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            uiState.workout?.id?.let { navigateToWorkoutSummary(it) }
        }
    }

    LaunchedEffect(uiState.workout) {
        uiState.workout?.let { workout ->
            while (true) {
                elapsedSeconds = ((Date().time - workout.date.time) / 1000).toInt()
                delay(1000L)
            }
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.workout?.name
                                ?: stringResource(PerformWorkoutDestination.titleRes),
                            style = MaterialTheme.typography.titleSmall
                        )
                        if (uiState.workout != null) {
                            Text(
                                text = formatElapsedTime(elapsedSeconds),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { showEndWorkoutDialog = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(R.string.end_workout))
                    }
                },
                scrollBehavior = scrollBehavior
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
                onAddSet = viewModel::addSet,
                onUpdateSet = viewModel::updateSet,
                onDeleteSet = viewModel::deleteSet,
                onReorder = viewModel::reorderExercises,
                onResolveSlot = viewModel::resolveSlot,
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
    onAddSet: (Int) -> Unit,
    onUpdateSet: (WorkoutSet) -> Unit,
    onDeleteSet: (WorkoutSet) -> Unit,
    onReorder: (Int, Int) -> Unit,
    onResolveSlot: (UnresolvedSlotItem, ExerciseWithCategories) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(
        lazyListState = lazyListState,
        onMove = { from, to -> onReorder(from.index, to.index) }
    )

    Column(modifier = modifier) {
        LazyColumn(
            state = lazyListState,
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
                key = { "exercise_${it.workoutExercise.id}" }
            ) { exerciseItem ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = "exercise_${exerciseItem.workoutExercise.id}"
                ) { isDragging ->
                    WorkoutExerciseCard(
                        exerciseItem = exerciseItem,
                        onAddSet = { onAddSet(exerciseItem.workoutExercise.id) },
                        onUpdateSet = onUpdateSet,
                        onDeleteSet = onDeleteSet,
                        dragHandle = {
                            Icon(
                                imageVector = Icons.Default.DragHandle,
                                contentDescription = stringResource(R.string.drag_to_reorder),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.draggableHandle()
                            )
                        },
                        modifier = Modifier.padding(
                            dimensionResource(R.dimen.padding_small)
                        )
                    )
                }
            }
            items(
                items = uiState.unresolvedSlots,
                key = { "slot_${it.routineSlot.id}" }
            ) { unresolvedSlot ->
                UnresolvedSlotCard(
                    unresolvedSlot = unresolvedSlot,
                    onExerciseSelected = { exercise ->
                        onResolveSlot(unresolvedSlot, exercise)
                    },
                    modifier = Modifier.padding(
                        dimensionResource(R.dimen.padding_small)
                    )
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
    val newExerciseLabel = stringResource(R.string.new_exercise)
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
                            searchQuery.ifBlank { newExerciseLabel }
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

@Composable
private fun WorkoutExerciseCard(
    exerciseItem: WorkoutExerciseItem,
    onAddSet: () -> Unit,
    onUpdateSet: (WorkoutSet) -> Unit,
    onDeleteSet: (WorkoutSet) -> Unit,
    dragHandle: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            ExerciseCardHeader(
                exerciseItem = exerciseItem,
                dragHandle = dragHandle
            )
            HorizontalDivider()
            ExerciseCardSetRows(
                exerciseItem = exerciseItem,
                onUpdateSet = onUpdateSet,
                onDeleteSet = onDeleteSet
            )
            HorizontalDivider()
            ExerciseCardFooter(onAddSet = onAddSet)
        }
    }
}

@Composable
private fun ExerciseCardHeader(
    exerciseItem: WorkoutExerciseItem,
    dragHandle: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exerciseItem.exercise.name,
                style = MaterialTheme.typography.titleMedium
            )
            exerciseItem.setScheme?.let { scheme ->
                Text(
                    text = scheme,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_medium)
                )
            ) {
                if (exerciseItem.exercise.prWeight > 0f) {
                    Text(
                        text = stringResource(
                            R.string.pr_weight_value,
                            exerciseItem.exercise.prWeight
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (exerciseItem.exercise.latestWeight > 0f) {
                    Text(
                        text = stringResource(
                            R.string.latest_weight_value,
                            exerciseItem.exercise.latestWeight
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        dragHandle()
    }
}

@Composable
private fun ExerciseCardSetRows(
    exerciseItem: WorkoutExerciseItem,
    onUpdateSet: (WorkoutSet) -> Unit,
    onDeleteSet: (WorkoutSet) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(
            vertical = dimensionResource(R.dimen.padding_small)
        )
    ) {
        if (exerciseItem.sets.isEmpty()) {
            Text(
                text = stringResource(R.string.no_sets_logged),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.padding_medium),
                    vertical = dimensionResource(R.dimen.padding_small)
                )
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                )
            ) {
                Text(
                    text = stringResource(R.string.set_header),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(32.dp)
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
                Spacer(modifier = Modifier.width(48.dp))
            }
            exerciseItem.sets.forEachIndexed { index, set ->
                SetRow(
                    setNumber = index + 1,
                    workoutSet = set,
                    onUpdateSet = onUpdateSet,
                    onDeleteSet = onDeleteSet
                )
            }
        }
    }
}

@Composable
private fun SetRow(
    setNumber: Int,
    workoutSet: WorkoutSet,
    onUpdateSet: (WorkoutSet) -> Unit,
    onDeleteSet: (WorkoutSet) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLogged = workoutSet.weight > 0f && workoutSet.reps > 0
    var weightText by rememberSaveable(workoutSet.id) {
        mutableStateOf(
            if (workoutSet.weight > 0f) workoutSet.weight.toString() else ""
        )
    }
    var repsText by rememberSaveable(workoutSet.id) {
        mutableStateOf(
            if (workoutSet.reps > 0) workoutSet.reps.toString() else ""
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_medium),
                vertical = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
    ) {
        Text(
            text = if (workoutSet.isAmrap) "$setNumber+" else "$setNumber",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(32.dp)
        )
        BasicTextField(
            value = weightText,
            onValueChange = { weightText = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .weight(1f)
                .height(35.dp)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        val newWeight = weightText.toFloatOrNull() ?: 0f
                        if (newWeight != workoutSet.weight) {
                            onUpdateSet(workoutSet.copy(weight = newWeight))
                        }
                    }
                },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (weightText.isEmpty()) {
                        Text(
                            text = "0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            }
        )
        BasicTextField(
            value = repsText,
            onValueChange = { repsText = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .weight(1f)
                .height(35.dp)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        val newReps = repsText.toIntOrNull() ?: 0
                        if (newReps != workoutSet.reps) {
                            onUpdateSet(workoutSet.copy(reps = newReps))
                        }
                    }
                },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (repsText.isEmpty()) {
                        Text(
                            text = if (workoutSet.isAmrap) "5+" else "0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            }
        )
        if (isLogged) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.set_logged),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onDeleteSet(workoutSet) }
            )
        } else {
            IconButton(onClick = { onDeleteSet(workoutSet) }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.delete_set),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ExerciseCardFooter(
    onAddSet: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onAddSet,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.padding_small))
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.add_set),
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_small))
        )
    }
}

@Composable
private fun RestTimerFab(
    modifier: Modifier = Modifier
) {
    var timerDurationSeconds by rememberSaveable { mutableStateOf(90) }
    var remainingSeconds by rememberSaveable { mutableStateOf(0) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var showDurationPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        } else if (isRunning && remainingSeconds == 0) {
            isRunning = false
            vibrateDevice(context)
            playNotificationSound(context)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(
                if (isRunning)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.secondaryContainer
            )
            .combinedClickable(
                onClick = {
                    if (isRunning) {
                        isRunning = false
                        remainingSeconds = 0
                    } else {
                        remainingSeconds = timerDurationSeconds
                        isRunning = true
                    }
                },
                onLongClick = { showDurationPicker = true }
            )
    ) {
        if (isRunning) {
            CircularProgressIndicator(
                progress = { remainingSeconds.toFloat() / timerDurationSeconds.toFloat() },
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
                trackColor = ProgressIndicatorDefaults.circularTrackColor,
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
            )
            Text(
                text = formatTimerSeconds(remainingSeconds),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = stringResource(R.string.rest_timer),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = formatTimerSeconds(timerDurationSeconds),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }

    if (showDurationPicker) {
        TimerDurationPickerDialog(
            currentDurationSeconds = timerDurationSeconds,
            onDurationSelected = { newDuration ->
                timerDurationSeconds = newDuration
                showDurationPicker = false
            },
            onDismiss = { showDurationPicker = false }
        )
    }
}

@Composable
private fun AddExerciseFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.add_exercise)
        )
    }
}

@Composable
private fun TimerDurationPickerDialog(
    currentDurationSeconds: Int,
    onDurationSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val presetDurations = listOf(30, 60, 90, 120, 180, 240, 300)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.rest_timer_duration)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                )
            ) {
                presetDurations.forEach { duration ->
                    TextButton(
                        onClick = { onDurationSelected(duration) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(formatTimerSeconds(duration))
                            if (duration == currentDurationSeconds) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    if (duration != presetDurations.last()) {
                        HorizontalDivider()
                    }
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

@Composable
private fun UnresolvedSlotCard(
    unresolvedSlot: UnresolvedSlotItem,
    onExerciseSelected: (ExerciseWithCategories) -> Unit,
    modifier: Modifier = Modifier
) {
    var showExercisePicker by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredExercises = remember(unresolvedSlot.availableExercises, searchQuery) {
        if (searchQuery.isBlank()) unresolvedSlot.availableExercises
        else unresolvedSlot.availableExercises.filter {
            it.exercise.name.contains(searchQuery, ignoreCase = true)
        }
    }

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = unresolvedSlot.routineSlot.categoryLabel
                            ?: stringResource(R.string.flexible_slot),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = unresolvedSlot.routineSlot.setScheme,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = stringResource(R.string.flexible_slot_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            unresolvedSlot.routineSlot.note?.let { note ->
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(stringResource(R.string.search_or_select_exercise))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showExercisePicker = true },
                readOnly = true,
                enabled = false
            )
        }
    }

    if (showExercisePicker) {
        AlertDialog(
            onDismissRequest = { showExercisePicker = false },
            title = {
                Text(
                    stringResource(
                        R.string.select_exercise_for_slot,
                        unresolvedSlot.routineSlot.categoryLabel
                            ?: stringResource(R.string.flexible_slot)
                    )
                )
            },
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
                                onClick = {
                                    onExerciseSelected(exerciseWithCategories)
                                    showExercisePicker = false
                                },
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
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showExercisePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

private fun formatTimerSeconds(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private fun formatElapsedTime(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

@RequiresPermission(Manifest.permission.VIBRATE)
private fun vibrateDevice(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createWaveform(
                longArrayOf(0, 300, 100, 300, 100, 300),
                -1
            )
        )
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(longArrayOf(0, 300, 100, 300, 100, 300), -1)
    }
}

private fun playNotificationSound(context: Context) {
    try {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(context, notification)
        ringtone.play()
    } catch (e: Exception) {
        // Silently fail if sound cannot be played
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutExerciseCardPreview() {
    LiftAssistantTheme {
        WorkoutExerciseCard(
            exerciseItem = WorkoutExerciseItem(
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
                        reps = 0,
                        weight = 0f,
                        isAmrap = true
                    )
                ),
                setScheme = "2x5, 1x5+"
            ),
            onAddSet = {},
            onUpdateSet = {},
            onDeleteSet = {},
            dragHandle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutExerciseCardEmptyPreview() {
    LiftAssistantTheme {
        WorkoutExerciseCard(
            exerciseItem = WorkoutExerciseItem(
                workoutExercise = WorkoutExercise(
                    id = 1,
                    workoutId = 1,
                    exerciseId = 1,
                    order = 0
                ),
                exercise = Exercise(
                    id = 1,
                    name = "Squat",
                    prWeight = 225f,
                    latestWeight = 215f
                ),
                sets = emptyList(),
                setScheme = "2x5, 1x5+"
            ),
            onAddSet = {},
            onUpdateSet = {},
            onDeleteSet = {},
            dragHandle = {}
        )
    }
}