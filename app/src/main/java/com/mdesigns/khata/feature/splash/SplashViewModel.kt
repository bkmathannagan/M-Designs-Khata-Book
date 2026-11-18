package com.mdesigns.khata.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.data.entity.BusinessProfileEntity
import com.mdesigns.khata.data.entity.CategoryEntity
import com.mdesigns.khata.data.repository.MoneyRepository
import com.mdesigns.khata.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashDestination {
    object Loading : SplashDestination
    object Onboarding : SplashDestination
    object Lock : SplashDestination
    object Home : SplashDestination
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferences: UserPreferencesDataStore,
    private val profileRepository: ProfileRepository,
    private val moneyRepository: MoneyRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            seedInitialDataIfNeeded()
        }
    }

    val destination = combine(
        preferences.isOnboarded,
        preferences.isAppLockEnabled
    ) { isOnboarded, isAppLockEnabled ->
        when {
            !isOnboarded -> SplashDestination.Onboarding
            isAppLockEnabled -> SplashDestination.Lock
            else -> SplashDestination.Home
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SplashDestination.Loading)

    private suspend fun seedInitialDataIfNeeded() {
        val defaultProfile = profileRepository.getDefaultProfile()
        if (defaultProfile == null) {
            val profileId = profileRepository.insertProfile(
                BusinessProfileEntity(
                    name = "My Shop / Business",
                    ownerName = "Proprietor",
                    website = "https://mdesignsrmd.com",
                    isDefault = true
                )
            )
            preferences.setActiveProfileId(profileId)

            val initialCategories = listOf(
                CategoryEntity(profileId = profileId, name = "Sales", type = "INCOME", sortOrder = 1),
                CategoryEntity(profileId = profileId, name = "Services", type = "INCOME", sortOrder = 2),
                CategoryEntity(profileId = profileId, name = "Rent", type = "EXPENSE", sortOrder = 3),
                CategoryEntity(profileId = profileId, name = "Salary", type = "EXPENSE", sortOrder = 4),
                CategoryEntity(profileId = profileId, name = "Electricity", type = "EXPENSE", sortOrder = 5),
                CategoryEntity(profileId = profileId, name = "Purchase", type = "EXPENSE", sortOrder = 6)
            )
            for (cat in initialCategories) {
                moneyRepository.insertCategory(cat)
            }
        }
    }
}
