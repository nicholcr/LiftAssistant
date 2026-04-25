package com.example.liftassistant.data.repos

import com.example.liftassistant.data.Category
import com.example.liftassistant.data.daos.CategoryDao
import kotlinx.coroutines.flow.Flow

class OfflineCategoryRepository(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override fun getAllCategoriesStream(): Flow<List<Category>> =
        categoryDao.getAllCategories()

    override fun getCategoryStream(id: Int): Flow<Category?> =
        categoryDao.getCategory(id)

    override suspend fun insertCategory(category: Category): Long =
        categoryDao.insert(category)

    override suspend fun insertAllCategories(categories: List<Category>) =
        categoryDao.insertAll(categories)

    override suspend fun updateCategory(category: Category) =
        categoryDao.update(category)

    override suspend fun deleteCategory(category: Category) =
        categoryDao.delete(category)

    override suspend fun getExerciseCountForCategory(categoryId: Int): Int =
        categoryDao.getExerciseCountForCategory(categoryId)
}