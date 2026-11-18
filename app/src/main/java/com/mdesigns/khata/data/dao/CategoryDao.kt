package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE profileId = :profileId AND isActive = 1 ORDER BY sortOrder ASC")
    fun getCategories(profileId: Long): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE profileId = :profileId")
    suspend fun getAllCategoriesList(profileId: Long): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}
