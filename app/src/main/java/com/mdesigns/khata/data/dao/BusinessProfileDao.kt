package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.BusinessProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessProfileDao {
    @Query("SELECT * FROM business_profiles ORDER BY id ASC")
    fun getAllProfiles(): Flow<List<BusinessProfileEntity>>

    @Query("SELECT * FROM business_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): BusinessProfileEntity?

    @Query("SELECT * FROM business_profiles WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultProfile(): BusinessProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: BusinessProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: BusinessProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: BusinessProfileEntity)
}
