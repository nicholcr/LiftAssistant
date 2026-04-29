package com.example.liftassistant.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.liftassistant.data.RoutineSlot
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineSlotDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(routineSlot: RoutineSlot)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(routineSlots: List<RoutineSlot>)

    @Update
    suspend fun update(routineSlot: RoutineSlot)

    @Delete
    suspend fun delete(routineSlot: RoutineSlot)

    @Query("SELECT * FROM routine_slots WHERE id = :id")
    fun getRoutineSlot(id: Int): Flow<RoutineSlot?>

    @Query("SELECT * FROM routine_slots WHERE routineId = :routineId ORDER BY 'order' ASC")
    fun getSlotsForRoutine(routineId: Int): Flow<List<RoutineSlot>>

    @Query("DELETE FROM routine_slots WHERE routineId = :routineId")
    suspend fun deleteAllSlotsForRoutine(routineId: Int)

    @Query("SELECT * FROM routine_slots WHERE categoryLabel = :categoryLabel")
    suspend fun getAllSlotsWithCategoryLabel(categoryLabel: String): List<RoutineSlot>
}