package com.example.liftassistant.ui.workout_routine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftassistant.data.ExerciseWithCategories
import com.example.liftassistant.data.RoutineSlot
import com.example.liftassistant.data.WorkoutRoutine
import com.example.liftassistant.data.repos.ExerciseRepository
import com.example.liftassistant.data.repos.RoutineSlotRepository
import com.example.liftassistant.data.repos.WorkoutRoutineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditWorkoutRoutineViewModel(
    private val workoutRoutineRepository: WorkoutRoutineRepository,
    private val routineSlotRepository: RoutineSlotRepository,
    private val exerciseRepository: ExerciseRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val routineId: Int = checkNotNull(
        savedStateHandle[EditWorkoutRoutineDestination.routineIdArg]
    )

    var formState by mutableStateOf(RoutineFormState())
        private set

    val availableExercises: StateFlow<List<ExerciseWithCategories>> =
        exerciseRepository.getAllExercisesWithCategoriesStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    init {
        viewModelScope.launch {
            loadRoutine()
        }
    }

    private suspend fun loadRoutine() {
        val routine = workoutRoutineRepository
            .getWorkoutRoutineStream(routineId)
            .filterNotNull()
            .first()

        val slots = routineSlotRepository
            .getSlotsForRoutineStream(routineId)
            .first()

        val slotFormStates = slots.map { slot ->
            val fixedExercise = slot.fixedExerciseId?.let { exerciseId ->
                exerciseRepository
                    .getExerciseWithCategoriesStream(exerciseId)
                    .filterNotNull()
                    .first()
                    .exercise
            }
            RoutineSlotFormState(
                id = slot.id,
                order = slot.order,
                setScheme = slot.setScheme,
                slotType = if (slot.fixedExerciseId != null)
                    SlotType.Fixed else SlotType.Flexible,
                fixedExercise = fixedExercise,
                categoryLabel = slot.categoryLabel ?: "",
                note = slot.note ?: "",
                isValid = true
            )
        }

        formState = RoutineFormState(
            name = routine.name,
            slots = slotFormStates,
            isValid = true
        )
    }

    fun updateName(name: String) {
        formState = formState.copy(
            name = name,
            isValid = validateForm(name, formState.slots)
        )
    }

    fun addSlot() {
        val newSlot = RoutineSlotFormState(order = formState.slots.size)
        val updatedSlots = formState.slots + newSlot
        formState = formState.copy(
            slots = updatedSlots,
            isValid = validateForm(formState.name, updatedSlots)
        )
    }

    fun removeSlot(index: Int) {
        val updatedSlots = formState.slots
            .toMutableList()
            .also { it.removeAt(index) }
            .mapIndexed { i, slot -> slot.copy(order = i) }
        formState = formState.copy(
            slots = updatedSlots,
            isValid = validateForm(formState.name, updatedSlots)
        )
    }

    fun updateSlot(index: Int, updatedSlot: RoutineSlotFormState) {
        val validatedSlot = updatedSlot.copy(isValid = validateSlot(updatedSlot))
        val updatedSlots = formState.slots.toMutableList().also {
            it[index] = validatedSlot
        }
        formState = formState.copy(
            slots = updatedSlots,
            isValid = validateForm(formState.name, updatedSlots)
        )
    }

    fun reorderSlots(fromIndex: Int, toIndex: Int) {
        val updatedSlots = formState.slots.toMutableList().also {
            val slot = it.removeAt(fromIndex)
            it.add(toIndex, slot)
        }.mapIndexed { i, slot -> slot.copy(order = i) }
        formState = formState.copy(slots = updatedSlots)
    }

    private fun validateSlot(slot: RoutineSlotFormState): Boolean {
        if (slot.setScheme.isBlank()) return false
        return when (slot.slotType) {
            SlotType.Fixed -> slot.fixedExercise != null
            SlotType.Flexible -> slot.categoryLabel.isNotBlank()
        }
    }

    private fun validateForm(name: String, slots: List<RoutineSlotFormState>): Boolean {
        if (name.isBlank()) return false
        return slots.all { validateSlot(it) }
    }

    suspend fun saveRoutine() {
        if (!validateForm(formState.name, formState.slots)) return
        workoutRoutineRepository.updateWorkoutRoutine(
            WorkoutRoutine(
                id = routineId,
                name = formState.name.trim()
            )
        )
        routineSlotRepository.deleteAllSlotsForRoutine(routineId)
        val slots = formState.slots.map { slot ->
            slot.toRoutineSlot(routineId)
        }
        if (slots.isNotEmpty()) {
            routineSlotRepository.insertAllRoutineSlots(slots)
        }
    }
}