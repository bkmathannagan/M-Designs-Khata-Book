package com.mdesigns.khata.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.core.utils.KhataBalanceCalculator
import com.mdesigns.khata.data.entity.BusinessProfileEntity
import com.mdesigns.khata.data.entity.TransactionEntity
import com.mdesigns.khata.data.repository.KhataRepository
import com.mdesigns.khata.data.repository.MoneyRepository
import com.mdesigns.khata.data.repository.ProfileRepository
import com.mdesigns.khata.domain.model.DashboardSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val khataRepository: KhataRepository,
    private val moneyRepository: MoneyRepository,
    private val profileRepository: ProfileRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    private val _activeProfile = MutableStateFlow<BusinessProfileEntity?>(null)
    val activeProfile: StateFlow<BusinessProfileEntity?> = _activeProfile.asStateFlow()

    private val _summary = MutableStateFlow(DashboardSummary())
    val summary: StateFlow<DashboardSummary> = _summary.asStateFlow()

    private val _recentTransactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val recentTransactions: StateFlow<List<TransactionEntity>> = _recentTransactions.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                loadProfileData(profileId)
            }
        }
    }

    private fun loadProfileData(profileId: Long) {
        viewModelScope.launch {
            _activeProfile.value = profileRepository.getProfileById(profileId) ?: profileRepository.getDefaultProfile()

            khataRepository.getPeopleSummaries(profileId).collectLatest { people ->
                val receivable = people.filter { it.status == KhataBalanceCalculator.BalanceStatus.YOU_COLLECT }.sumOf { it.netBalance }
                val payable = people.filter { it.status == KhataBalanceCalculator.BalanceStatus.YOU_GIVE }.sumOf { kotlin.math.abs(it.netBalance) }
                val net = receivable - payable
                val settled = people.count { it.status == KhataBalanceCalculator.BalanceStatus.SETTLED }
                val pending = people.size - settled
                val customers = people.count { it.person.category == "Customer" }
                val suppliers = people.count { it.person.category == "Supplier" }

                val todayIncome = moneyRepository.getTodayIncome(profileId)
                val todayExpense = moneyRepository.getTodayExpense(profileId)
                val monthIncome = moneyRepository.getMonthIncome(profileId)
                val monthExpense = moneyRepository.getMonthExpense(profileId)

                _summary.value = DashboardSummary(
                    totalReceivable = receivable,
                    totalPayable = payable,
                    netBalance = net,
                    todayIncome = todayIncome,
                    todayExpense = todayExpense,
                    monthIncome = monthIncome,
                    monthExpense = monthExpense,
                    customerCount = customers,
                    supplierCount = suppliers,
                    settledCount = settled,
                    pendingCount = pending
                )
            }
        }

        viewModelScope.launch {
            khataRepository.getRecentTransactions(profileId, 10).collectLatest { txs ->
                _recentTransactions.value = txs
            }
        }
    }

    fun updateBusinessProfile(
        name: String,
        ownerName: String,
        mobile: String,
        email: String,
        gstin: String,
        address: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val current = _activeProfile.value
                val profileToSave = current?.copy(
                    name = name.trim().ifEmpty { "M Designs Khata" },
                    ownerName = ownerName.trim(),
                    mobile = mobile.trim(),
                    email = email.trim(),
                    gstin = gstin.trim(),
                    address = address.trim(),
                    updatedAt = System.currentTimeMillis()
                ) ?: BusinessProfileEntity(
                    name = name.trim().ifEmpty { "M Designs Khata" },
                    ownerName = ownerName.trim(),
                    mobile = mobile.trim(),
                    email = email.trim(),
                    gstin = gstin.trim(),
                    address = address.trim(),
                    isDefault = true
                )

                val savedId = if (profileToSave.id > 0) {
                    profileRepository.updateProfile(profileToSave)
                    profileToSave.id
                } else {
                    val newId = profileRepository.insertProfile(profileToSave)
                    preferences.setActiveProfileId(newId)
                    newId
                }

                _activeProfile.value = profileRepository.getProfileById(savedId) ?: profileRepository.getDefaultProfile()
                onComplete(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }
}
