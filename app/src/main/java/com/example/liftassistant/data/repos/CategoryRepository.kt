package com.example.liftassistant.data.repos

import com.example.liftassistant.data.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategoriesStream(): Flow<List<Category>>
    fun getCategoryStream(id: Int): Flow<Category?>
    suspend fun insertCategory(category: Category): Long
    suspend fun insertAllCategories(categories: List<Category>)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun getExerciseCountForCategory(categoryId: Int): Int
}