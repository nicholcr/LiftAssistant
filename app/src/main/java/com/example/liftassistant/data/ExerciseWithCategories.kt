package com.example.liftassistant.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ExerciseWithCategories(
    @Embedded val exercise: Exercise,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ExerciseCategory::class,
            parentColumn = "exerciseId",
            entityColumn = "categoryId"
        )
    )
    val categories: List<Category>
)
