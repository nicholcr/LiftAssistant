package com.example.liftassistant.ui.workout

import android.icu.util.Calendar
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.ExerciseWithCategories
import com.example.liftassistant.data.RoutineSlot
import com.example.liftassistant.data.Workout
import com.example.liftassistant.data.WorkoutExercise
import com.example.liftassistant.data.WorkoutSet
import com.example.liftassistant.data.repos.ExerciseRepository
import com.example.liftassistant.data.repos.RoutineSlotRepository
import com.example.liftassistant.data.repos.WorkoutExerciseRepository
import com.example.liftassistant.data.repos.WorkoutRepository
import com.example.liftassistant.data.repos.WorkoutRoutineRepository
import com.example.liftassistant.data.repos.WorkoutSetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WorkoutExerciseItem(
    val workoutExercise: WorkoutExercise,
    val exercise: Exercise,
    val categories: List<String> = emptyList(),
    val sets: List<WorkoutSet> = emptyList(),
    val setScheme: String? = null
)

data class UnresolvedSlotItem(
    val routineSlot: RoutineSlot,
    val availableExercises: List<ExerciseWithCategories>,
    val workoutExerciseId: Int = 0
)

data class PerformWorkoutUiState(
    val workout: Workout? = null,
    val exerciseItems: List<WorkoutExerciseItem> = emptyList(),
    val unresolvedSlots: List<UnresolvedSlotItem> = emptyList(),
    val availableExercises: List<ExerciseWithCategories> = emptyList(),
    val isSaving: Boolean = false,
    val isComplete: Boolean = false
)

class PerformWorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
    private val workoutExerciseRepository: WorkoutExerciseRepository,
    private val workoutSetRepository: WorkoutSetRepository,
    private val exerciseRepository: ExerciseRepository,
    private val routineSlotRepository: RoutineSlotRepository,
    private val workoutRoutineRepository: WorkoutRoutineRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutId: Int? = savedStateHandle.get<Int>(PerformWorkoutDestination.workoutIdArg)
    private val routineId: Int? = savedStateHandle.get<Int>(PerformWorkoutDestination.routineIdArg)
        ?.takeIf { it != -1 }

    private val _uiState = MutableStateFlow(PerformWorkoutUiState())
    val uiState: StateFlow<PerformWorkoutUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            android.util.Log.d("LiftAssistant", "PerformWorkoutViewModel init: workoutId=$workoutId, routineId=$routineId")
            when {
                workoutId != null -> loadExistingWorkout(workoutId)
                else -> createNewWorkout(routineId)
            }
        }
    }

    private suspend fun loadExistingWorkout(id: Int) {
        val workout = workoutRepository.getWorkoutStream(id)
            .filterNotNull()
            .first()
        val workoutExercises = workoutExerciseRepository
            .getExercisesForWorkoutStream(id)
            .first()
        val availableExercises = exerciseRepository
            .getAllExercisesWithCategoriesStream()
            .first()
        val exerciseItems = mutableListOf<WorkoutExerciseItem>()
        val unresolvedSlots = mutableListOf<UnresolvedSlotItem>()

        workoutExercises.forEach { workoutExercise ->
            if (workoutExercise.exerciseId != null) {
                exerciseItems.add(buildExerciseItem(workoutExercise))
            } else {
                val slot = workoutExercise.routineSlotId?.let { slotId ->
                    routineSlotRepository.getRoutineSlotStream(slotId).first()
                }
                if (slot != null) {
                    val filteredExercises = availableExercises.filter { ewc ->
                        ewc.categories.any { it.name == slot.categoryLabel }
                    }
                    unresolvedSlots.add(
                        UnresolvedSlotItem(
                            routineSlot = slot,
                            availableExercises = filteredExercises
                                .ifEmpty { availableExercises },
                            workoutExerciseId = workoutExercise.id
                        )
                    )
                }
            }
        }

        _uiState.update {
            it.copy(
                workout = workout,
                exerciseItems = exerciseItems,
                unresolvedSlots = unresolvedSlots,
                availableExercises = availableExercises
            )
        }
    }

    private suspend fun createNewWorkout(routineId: Int? = null) {
        val existingInProgressWorkout = workoutRepository
            .getInProgressWorkoutStream()
            .first()

        if (existingInProgressWorkout != null) {
            loadExistingWorkout(existingInProgressWorkout.id)
            return
        }

        val routineName = routineId?.let { id ->
            workoutRoutineRepository.getWorkoutRoutineStream(id)
                .filterNotNull()
                .first()
                .name
        }

        val availableExercises = exerciseRepository
            .getAllExercisesWithCategoriesStream()
            .first()

        val workout = Workout(name = generateWorkoutName(routineName))
        val newWorkoutId = workoutRepository.insertWorkout(workout)
        val newWorkout = workoutRepository
            .getWorkoutStream(newWorkoutId.toInt())
            .filterNotNull()
            .first()

        val exerciseItems = mutableListOf<WorkoutExerciseItem>()
        val unresolvedSlots = mutableListOf<UnresolvedSlotItem>()

        if (routineId != null) {
            val slots = routineSlotRepository
                .getSlotsForRoutineStream(routineId)
                .first()

            slots.forEachIndexed { index, slot ->
                android.util.Log.d("LiftAssistant", "Processing slot id=${slot.id}, fixedExerciseId=${slot.fixedExerciseId}, categoryLabel=${slot.categoryLabel}")
                when {
                    slot.fixedExerciseId != null -> {
                        android.util.Log.d("LiftAssistant", "Slot id=${slot.id}, fixedExerciseId=${slot.fixedExerciseId}, routineId=${slot.routineId}")
                        val workoutExercise = WorkoutExercise(
                            workoutId = newWorkoutId.toInt(),
                            exerciseId = slot.fixedExerciseId,
                            order = index,
                            routineSlotId = slot.id
                        )
                        android.util.Log.d("LiftAssistant", "Inserting WorkoutExercise: workoutId=${newWorkoutId.toInt()}, exerciseId=${slot.fixedExerciseId}")
                        val weId = workoutExerciseRepository
                            .insertWorkoutExercise(workoutExercise)
                        android.util.Log.d("LiftAssistant", "Inserted placeholder WorkoutExercise with id=$weId for slot ${slot.id}")
                        val sets = prePopulateSets(slot, weId.toInt())
                        android.util.Log.d("LiftAssistant", "Sets pre-populated: ${sets.size}")
                        val exerciseWithCategories = exerciseRepository
                            .getExerciseWithCategoriesStream(slot.fixedExerciseId)
                            .filterNotNull()
                            .first()
                        exerciseItems.add(
                            WorkoutExerciseItem(
                                workoutExercise = workoutExercise.copy(id = weId.toInt()),
                                exercise = exerciseWithCategories.exercise,
                                categories = exerciseWithCategories.categories.map { it.name },
                                sets = sets,
                                setScheme = slot.setScheme
                            )
                        )
                    }
                    slot.categoryLabel != null -> {
                        android.util.Log.d("LiftAssistant", "Creating placeholder for flexible slot id=${slot.id}, categoryLabel=${slot.categoryLabel}")
                        val workoutExercise = WorkoutExercise(
                            workoutId = newWorkoutId.toInt(),
                            exerciseId = null,
                            order = index,
                            routineSlotId = slot.id
                        )
                        val weId = workoutExerciseRepository.insertWorkoutExercise(workoutExercise)
                        android.util.Log.d("LiftAssistant", "Placeholder inserted with id=$weId")
                        val filteredExercises = availableExercises.filter { ewc ->
                            ewc.categories.any { it.name == slot.categoryLabel }
                        }
                        unresolvedSlots.add(
                            UnresolvedSlotItem(
                                routineSlot = slot,
                                availableExercises = filteredExercises.ifEmpty { availableExercises },
                                workoutExerciseId = weId.toInt()
                            )
                        )
                        android.util.Log.d("LiftAssistant", "UnresolvedSlotItem added with workoutExerciseId=${weId.toInt()}")
                    }
                    else -> {
                        android.util.Log.d("LiftAssistant", "Slot ${slot.id} has both null - skipping")
                    }
                }
            }
        }

        _uiState.update {
            it.copy(
                workout = newWorkout,
                exerciseItems = exerciseItems,
                unresolvedSlots = unresolvedSlots,
                availableExercises = availableExercises
            )
        }
    }

    private suspend fun buildExerciseItem(
        workoutExercise: WorkoutExercise
    ): WorkoutExerciseItem {
        val exerciseId = workoutExercise.exerciseId ?: return WorkoutExerciseItem(
            workoutExercise = workoutExercise,
            exercise = Exercise(name = ""),
            sets = emptyList()
        )
        val exerciseWithCategories = exerciseRepository
            .getExerciseWithCategoriesStream(workoutExercise.exerciseId)
            .filterNotNull()
            .first()
        val sets = workoutSetRepository
            .getSetsForWorkoutExerciseStream(workoutExercise.id)
            .first()
        val setScheme = workoutExercise.routineSlotId?.let { slotId ->
            routineSlotRepository.getRoutineSlotStream(slotId).first()?.setScheme
        }
        return WorkoutExerciseItem(
            workoutExercise = workoutExercise,
            exercise = exerciseWithCategories.exercise,
            categories = exerciseWithCategories.categories.map { it.name },
            sets = sets,
            setScheme = setScheme
        )
    }

    private suspend fun prePopulateSets(
        slot: RoutineSlot,
        workoutExerciseId: Int
    ): List<WorkoutSet> {
        val sets = parseSetScheme(slot.setScheme, workoutExerciseId)
        if (sets.isNotEmpty()) {
            workoutSetRepository.insertAllWorkoutSets(sets)
            return workoutSetRepository
                .getSetsForWorkoutExerciseStream(workoutExerciseId)
                .first()
        }
        return sets
    }

    private fun parseSetScheme(scheme: String, workoutExerciseId: Int): List<WorkoutSet> {
        val sets = mutableListOf<WorkoutSet>()
        var order = 0
        scheme.split(",").map { it.trim() }.forEach { part ->
            val amrap = part.endsWith("+")
            val cleanPart = part.removeSuffix("+").trim()
            val xIndex = cleanPart.indexOf('x', ignoreCase = true)
            if (xIndex != -1) {
                val count = cleanPart.substring(0, xIndex).trim().toIntOrNull() ?: 1
                val repsStr = cleanPart.substring(xIndex + 1).trim()
                val reps = repsStr.split("-").first().trim().toIntOrNull() ?: 0
                repeat(count) {
                    sets.add(
                        WorkoutSet(
                            workoutExerciseId = workoutExerciseId,
                            order = order++,
                            reps = 0,
                            weight = 0f,
                            isAmrap = amrap
                        )
                    )
                }
            }
        }
        return sets
    }

    private fun generateWorkoutName(routineName: String? = null): String {
        val timeOfDay = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Morning Workout"
            in 12..16 -> "Afternoon Workout"
            in 17..20 -> "Evening Workout"
            else -> "Night Workout"
        }
        return routineName ?: timeOfDay
    }

    fun addExercise(exerciseWithCategories: ExerciseWithCategories) {
        viewModelScope.launch {
            val workoutId = _uiState.value.workout?.id ?: return@launch
            val order = _uiState.value.exerciseItems.size
            val workoutExercise = WorkoutExercise(
                workoutId = workoutId,
                exerciseId = exerciseWithCategories.exercise.id,
                order = order
            )
            val workoutExerciseId = workoutExerciseRepository
                .insertWorkoutExercise(workoutExercise)
            _uiState.update { state ->
                state.copy(
                    exerciseItems = state.exerciseItems + WorkoutExerciseItem(
                        workoutExercise = workoutExercise.copy(
                            id = workoutExerciseId.toInt()
                        ),
                        exercise = exerciseWithCategories.exercise,
                        categories = exerciseWithCategories.categories.map { it.name }
                    )
                )
            }
        }
    }

    fun quickAddExercise(name: String) {
        viewModelScope.launch {
            val exerciseId = exerciseRepository.insertExercise(
                Exercise(name = name.trim())
            )
            val exerciseWithCategories = exerciseRepository
                .getExerciseWithCategoriesStream(exerciseId.toInt())
                .filterNotNull()
                .first()
            addExercise(exerciseWithCategories)
        }
    }

    fun addSet(workoutExerciseId: Int) {
        viewModelScope.launch {
            val exerciseItem = _uiState.value.exerciseItems
                .find { it.workoutExercise.id == workoutExerciseId }
                ?: return@launch
            val order = exerciseItem.sets.size
            val newSet = WorkoutSet(
                workoutExerciseId = workoutExerciseId,
                order = order,
                reps = 0,
                weight = 0f
            )
            val newSetId = workoutSetRepository.insertWorkoutSet(newSet)
            val insertedSet = newSet.copy(id = newSetId.toInt())
            _uiState.update { state ->
                state.copy(
                    exerciseItems = state.exerciseItems.map { item ->
                        if (item.workoutExercise.id == workoutExerciseId) {
                            item.copy(sets = item.sets + insertedSet)
                        } else item
                    }
                )
            }
        }
    }

    fun updateSet(updatedSet: WorkoutSet) {
        viewModelScope.launch {
            workoutSetRepository.updateWorkoutSet(updatedSet)
            _uiState.update { state ->
                state.copy(
                    exerciseItems = state.exerciseItems.map { item ->
                        if (item.workoutExercise.id == updatedSet.workoutExerciseId) {
                            item.copy(
                                sets = item.sets.map { set ->
                                    if (set.id == updatedSet.id) updatedSet else set
                                }
                            )
                        } else item
                    }
                )
            }
            updateExerciseWeights(updatedSet)
        }
    }

    fun deleteSet(workoutSet: WorkoutSet) {
        viewModelScope.launch {
            workoutSetRepository.deleteWorkoutSet(workoutSet)
            _uiState.update { state ->
                state.copy(
                    exerciseItems = state.exerciseItems.map { item ->
                        if (item.workoutExercise.id == workoutSet.workoutExerciseId) {
                            val updatedSets = item.sets
                                .filter { it.id != workoutSet.id }
                                .mapIndexed { index, set -> set.copy(order = index) }
                            updatedSets.forEach { workoutSetRepository.updateWorkoutSet(it) }
                            item.copy(sets = updatedSets)
                        } else item
                    }
                )
            }
        }
    }

    fun reorderExercises(fromIndex: Int, toIndex: Int) {
        val currentItems = _uiState.value.exerciseItems
        if (fromIndex < 0 || fromIndex >= currentItems.size ||
            toIndex < 0 || toIndex >= currentItems.size) return

        val mutableItems = currentItems.toMutableList()
        val item = mutableItems.removeAt(fromIndex)
        mutableItems.add(toIndex, item)
        val reorderedItems = mutableItems.mapIndexed { index, exerciseItem ->
            exerciseItem.copy(
                workoutExercise = exerciseItem.workoutExercise.copy(order = index)
            )
        }
        _uiState.update { it.copy(exerciseItems = reorderedItems) }
        viewModelScope.launch {
            reorderedItems.forEach { exerciseItem ->
                workoutExerciseRepository.updateWorkoutExercise(
                    exerciseItem.workoutExercise
                )
            }
        }
    }

    fun endWorkout() {
        viewModelScope.launch {
            val workoutId = _uiState.value.workout?.id ?: return@launch
            _uiState.update { it.copy(isSaving = true) }
            workoutRepository.setEndTime(workoutId, Date())
            _uiState.update { it.copy(isSaving = false, isComplete = true) }
        }
    }

    fun resolveSlot(
        unresolvedSlot: UnresolvedSlotItem,
        exerciseWithCategories: ExerciseWithCategories
    ) {
        viewModelScope.launch {
            val workoutId = _uiState.value.workout?.id ?: return@launch

            // Update just the exerciseId column directly
            workoutExerciseRepository.updateExerciseId(
                workoutExerciseId = unresolvedSlot.workoutExerciseId,
                exerciseId = exerciseWithCategories.exercise.id
            )

            val sets = prePopulateSets(
                unresolvedSlot.routineSlot,
                unresolvedSlot.workoutExerciseId
            )
            val newItem = WorkoutExerciseItem(
                workoutExercise = WorkoutExercise(
                    id = unresolvedSlot.workoutExerciseId,
                    workoutId = workoutId,
                    exerciseId = exerciseWithCategories.exercise.id,
                    order = unresolvedSlot.routineSlot.order,
                    routineSlotId = unresolvedSlot.routineSlot.id
                ),
                exercise = exerciseWithCategories.exercise,
                categories = exerciseWithCategories.categories.map { it.name },
                sets = sets,
                setScheme = unresolvedSlot.routineSlot.setScheme
            )
            _uiState.update { state ->
                state.copy(
                    exerciseItems = state.exerciseItems + newItem,
                    unresolvedSlots = state.unresolvedSlots - unresolvedSlot
                )
            }
        }
    }

    private suspend fun updateExerciseWeights(updatedSet: WorkoutSet) {
        val exerciseItem = _uiState.value.exerciseItems
            .find { it.workoutExercise.id == updatedSet.workoutExerciseId }
            ?: return
        val exercise = exerciseItem.exercise
        val allWeights = exerciseItem.sets.map { it.weight }
        val newPr = allWeights.maxOrNull() ?: 0f
        val newLatest = updatedSet.weight
        if (newPr > exercise.prWeight || newLatest != exercise.latestWeight) {
            val updatedExercise = exercise.copy(
                prWeight = maxOf(newPr, exercise.prWeight),
                latestWeight = newLatest
            )
            exerciseRepository.updateExercise(updatedExercise)
            _uiState.update { state ->
                state.copy(
                    exerciseItems = state.exerciseItems.map { item ->
                        if (item.workoutExercise.id == updatedSet.workoutExerciseId) {
                            item.copy(exercise = updatedExercise)
                        } else item
                    }
                )
            }
        }
    }
}