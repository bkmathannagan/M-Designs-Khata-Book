package com.mdesigns.khata.feature.more.recyclebin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.data.entity.LedgerPersonEntity
import com.mdesigns.khata.data.entity.MoneyTransactionEntity
import com.mdesigns.khata.data.entity.TransactionEntity
import com.mdesigns.khata.data.repository.KhataRepository
import com.mdesigns.khata.data.repository.MoneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecycleBinViewModel @Inject constructor(
    private val repository: KhataRepository,
    private val moneyRepository: MoneyRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    private val _deletedPeople = MutableStateFlow<List<LedgerPersonEntity>>(emptyList())
    val deletedPeople: StateFlow<List<LedgerPersonEntity>> = _deletedPeople.asStateFlow()

    private val _deletedTransactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val deletedTransactions: StateFlow<List<TransactionEntity>> = _deletedTransactions.asStateFlow()

    private val _deletedMoneyTransactions = MutableStateFlow<List<MoneyTransactionEntity>>(emptyList())
    val deletedMoneyTransactions: StateFlow<List<MoneyTransactionEntity>> = _deletedMoneyTransactions.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                repository.getDeletedPeople(profileId).collectLatest { _deletedPeople.value = it }
            }
        }
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                repository.getDeletedTransactions(profileId).collectLatest { _deletedTransactions.value = it }
            }
        }
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                moneyRepository.getDeletedMoneyTransactions(profileId).collectLatest { _deletedMoneyTransactions.value = it }
            }
        }
    }

    fun restorePerson(id: Long) = viewModelScope.launch { repository.restorePerson(id) }
    fun permanentlyDeletePerson(id: Long) = viewModelScope.launch { repository.permanentlyDeletePerson(id) }

    fun restoreTransaction(id: Long) = viewModelScope.launch { repository.restoreTransaction(id) }
    fun permanentlyDeleteTransaction(id: Long) = viewModelScope.launch { repository.permanentlyDeleteTransaction(id) }

    fun restoreMoneyTransaction(id: Long) = viewModelScope.launch { moneyRepository.restoreMoneyTransaction(id) }
    fun permanentlyDeleteMoneyTransaction(id: Long) = viewModelScope.launch { moneyRepository.permanentlyDeleteMoneyTransaction(id) }
}

