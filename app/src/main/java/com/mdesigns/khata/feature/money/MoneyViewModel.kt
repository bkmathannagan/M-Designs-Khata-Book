package com.mdesigns.khata.feature.money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.data.entity.CategoryEntity
import com.mdesigns.khata.data.entity.MoneyTransactionEntity
import com.mdesigns.khata.data.repository.MoneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoneyViewModel @Inject constructor(
    private val repository: MoneyRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<MoneyTransactionEntity>>(emptyList())
    val transactions: StateFlow<List<MoneyTransactionEntity>> = _transactions.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

    val totalIncome = _transactions.map { list -> list.filter { it.type == "INCOME" }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense = _transactions.map { list -> list.filter { it.type == "EXPENSE" }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val isAppLockEnabled: StateFlow<Boolean> = preferences.isAppLockEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val appLockPinHash: StateFlow<String?> = preferences.appLockPinHash
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                repository.getAllMoneyTransactions(profileId).collectLatest { txs ->
                    _transactions.value = txs
                }
            }
        }
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                repository.getCategories(profileId).collectLatest { cats ->
                    _categories.value = cats
                }
            }
        }
    }

    fun addTransaction(type: String, amount: Double, category: String, description: String, paymentMode: String) {
        viewModelScope.launch {
            val profileId = preferences.activeProfileId.first()
            val entity = MoneyTransactionEntity(
                profileId = profileId,
                type = type,
                amount = amount,
                category = category,
                description = description.trim(),
                paymentMode = paymentMode
            )
            repository.insertMoneyTransaction(entity)
        }
    }

    fun updateTransaction(transaction: MoneyTransactionEntity) {
        viewModelScope.launch {
            repository.updateMoneyTransaction(
                transaction.copy(updatedAt = System.currentTimeMillis())
            )
        }
    }

    fun duplicateTransaction(tx: MoneyTransactionEntity) {
        viewModelScope.launch {
            val newEntity = tx.copy(
                id = 0,
                date = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.insertMoneyTransaction(newEntity)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.softDeleteMoneyTransaction(id)
        }
    }
}
