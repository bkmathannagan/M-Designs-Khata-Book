package com.mdesigns.khata.data.repository

import com.mdesigns.khata.data.dao.BusinessProfileDao
import com.mdesigns.khata.data.entity.BusinessProfileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileDao: BusinessProfileDao
) {
    fun getAllProfiles(): Flow<List<BusinessProfileEntity>> = profileDao.getAllProfiles()
    suspend fun getProfileById(id: Long): BusinessProfileEntity? = profileDao.getProfileById(id)
    suspend fun getDefaultProfile(): BusinessProfileEntity? = profileDao.getDefaultProfile()
    suspend fun insertProfile(profile: BusinessProfileEntity): Long = profileDao.insertProfile(profile)
    suspend fun updateProfile(profile: BusinessProfileEntity) = profileDao.updateProfile(profile)
    suspend fun deleteProfile(profile: BusinessProfileEntity) = profileDao.deleteProfile(profile)
}
