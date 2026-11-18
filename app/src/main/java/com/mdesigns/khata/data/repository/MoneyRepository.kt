package com.mdesigns.khata.data.repository

import com.mdesigns.khata.core.utils.DateUtils
import com.mdesigns.khata.data.dao.CategoryDao
import com.mdesigns.khata.data.dao.MoneyTransactionDao
import com.mdesigns.khata.data.entity.CategoryEntity
import com.mdesigns.khata.data.entity.MoneyTransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoneyRepository @Inject constructor(
    private val moneyDao: MoneyTransactionDao,
    private val categoryDao: CategoryDao
) {
    fun getAllMoneyTransactions(profileId: Long): Flow<List<MoneyTransactionEntity>> =
        moneyDao.getAllMoneyTransactions(profileId)

    fun getDeletedMoneyTransactions(profileId: Long): Flow<List<MoneyTransactionEntity>> =
        moneyDao.getDeletedMoneyTransactions(profileId)

    fun getCategories(profileId: Long): Flow<List<CategoryEntity>> =
        categoryDao.getCategories(profileId)

    suspend fun insertMoneyTransaction(transaction: MoneyTransactionEntity): Long =
        moneyDao.insertMoneyTransaction(transaction)

    suspend fun updateMoneyTransaction(transaction: MoneyTransactionEntity) =
        moneyDao.updateMoneyTransaction(transaction)

    suspend fun softDeleteMoneyTransaction(id: Long) = moneyDao.softDeleteMoneyTransaction(id)
    suspend fun restoreMoneyTransaction(id: Long) = moneyDao.restoreMoneyTransaction(id)
    suspend fun permanentlyDeleteMoneyTransaction(id: Long) = moneyDao.permanentlyDeleteMoneyTransaction(id)

    suspend fun insertCategory(category: CategoryEntity): Long = categoryDao.insertCategory(category)
    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)

    suspend fun getTodayIncome(profileId: Long): Double {
        val start = DateUtils.getStartOfDay()
        val end = System.currentTimeMillis()
        return moneyDao.getTotalIncomeBetween(profileId, start, end)
    }

    suspend fun getTodayExpense(profileId: Long): Double {
        val start = DateUtils.getStartOfDay()
        val end = System.currentTimeMillis()
        return moneyDao.getTotalExpenseBetween(profileId, start, end)
    }

    suspend fun getMonthIncome(profileId: Long): Double {
        val start = DateUtils.getStartOfMonth()
        val end = System.currentTimeMillis()
        return moneyDao.getTotalIncomeBetween(profileId, start, end)
    }

    suspend fun getMonthExpense(profileId: Long): Double {
        val start = DateUtils.getStartOfMonth()
        val end = System.currentTimeMillis()
        return moneyDao.getTotalExpenseBetween(profileId, start, end)
    }
}
