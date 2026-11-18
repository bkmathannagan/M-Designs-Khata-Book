package com.mdesigns.khata.feature.ledger

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.core.utils.KhataBalanceCalculator
import com.mdesigns.khata.data.entity.BusinessProfileEntity
import com.mdesigns.khata.data.entity.LedgerPersonEntity
import com.mdesigns.khata.data.entity.TransactionEntity
import com.mdesigns.khata.data.repository.KhataRepository
import com.mdesigns.khata.data.repository.ProfileRepository
import com.mdesigns.khata.domain.model.LedgerEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LedgerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: KhataRepository,
    private val profileRepository: ProfileRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    val personId: Long = savedStateHandle.get<String>("personId")?.toLongOrNull() ?: 0L

    private val _person = MutableStateFlow<LedgerPersonEntity?>(null)
    val person: StateFlow<LedgerPersonEntity?> = _person.asStateFlow()

    private val _businessProfile = MutableStateFlow<BusinessProfileEntity?>(null)
    val businessProfile: StateFlow<BusinessProfileEntity?> = _businessProfile.asStateFlow()

    private val _entries = MutableStateFlow<List<LedgerEntry>>(emptyList())
    val entries: StateFlow<List<LedgerEntry>> = _entries.asStateFlow()

    private val _balanceResult = MutableStateFlow<KhataBalanceCalculator.BalanceResult?>(null)
    val balanceResult: StateFlow<KhataBalanceCalculator.BalanceResult?> = _balanceResult.asStateFlow()

    init {
        loadLedger()
    }

    private fun loadLedger() {
        viewModelScope.launch {
            val p = repository.getPersonById(personId)
            _person.value = p

            val profileId = preferences.activeProfileId.first()
            _businessProfile.value = profileRepository.getProfileById(profileId)

            if (p != null) {
                repository.getPersonLedger(personId, p.openingBalance, p.isOpeningBalanceDebit).collectLatest { ledgerEntries ->
                    _entries.value = ledgerEntries

                    val totalDebit = ledgerEntries.filter { it.transaction.type == "DEBIT" }.sumOf { it.transaction.amount }
                    val totalCredit = ledgerEntries.filter { it.transaction.type == "CREDIT" }.sumOf { it.transaction.amount }

                    _balanceResult.value = KhataBalanceCalculator.calculate(
                        openingBalance = p.openingBalance,
                        isOpeningBalanceDebit = p.isOpeningBalanceDebit,
                        totalDebit = totalDebit,
                        totalCredit = totalCredit
                    )
                }
            }
        }
    }

    fun addTransaction(
        type: String,
        amount: Double,
        description: String,
        paymentMode: String,
        date: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            val profileId = preferences.activeProfileId.first()
            val tx = TransactionEntity(
                profileId = profileId,
                personId = personId,
                type = type,
                amount = amount,
                date = date,
                description = description.trim(),
                paymentMode = paymentMode
            )
            repository.insertTransaction(tx)
        }
    }

    fun updateTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(tx)
        }
    }

    fun deleteTransaction(txId: Long) {
        viewModelScope.launch {
            repository.softDeleteTransaction(txId)
        }
    }

    fun duplicateTransaction(txId: Long) {
        viewModelScope.launch {
            repository.duplicateTransaction(txId)
        }
    }

    fun convertTransactionType(txId: Long) {
        viewModelScope.launch {
            repository.convertTransactionType(txId)
        }
    }
}
