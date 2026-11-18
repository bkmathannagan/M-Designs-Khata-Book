package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.MoneyTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoneyTransactionDao {
    @Query("SELECT * FROM money_transactions WHERE profileId = :profileId AND isDeleted = 0 ORDER BY date DESC")
    fun getAllMoneyTransactions(profileId: Long): Flow<List<MoneyTransactionEntity>>

    @Query("SELECT * FROM money_transactions WHERE profileId = :profileId AND isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedMoneyTransactions(profileId: Long): Flow<List<MoneyTransactionEntity>>

    @Query("SELECT * FROM money_transactions WHERE profileId = :profileId")
    suspend fun getAllMoneyTransactionsList(profileId: Long): List<MoneyTransactionEntity>

    @Query("SELECT * FROM money_transactions WHERE profileId = :profileId AND isDeleted = 0 AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getMoneyTransactionsBetween(profileId: Long, startDate: Long, endDate: Long): Flow<List<MoneyTransactionEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM money_transactions WHERE profileId = :profileId AND type = 'INCOME' AND isDeleted = 0 AND date >= :startDate AND date <= :endDate")
    suspend fun getTotalIncomeBetween(profileId: Long, startDate: Long, endDate: Long): Double

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM money_transactions WHERE profileId = :profileId AND type = 'EXPENSE' AND isDeleted = 0 AND date >= :startDate AND date <= :endDate")
    suspend fun getTotalExpenseBetween(profileId: Long, startDate: Long, endDate: Long): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoneyTransaction(transaction: MoneyTransactionEntity): Long

    @Update
    suspend fun updateMoneyTransaction(transaction: MoneyTransactionEntity)

    @Query("UPDATE money_transactions SET isDeleted = 1, deletedAt = :timestamp WHERE id = :id")
    suspend fun softDeleteMoneyTransaction(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE money_transactions SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restoreMoneyTransaction(id: Long)

    @Query("DELETE FROM money_transactions WHERE id = :id")
    suspend fun permanentlyDeleteMoneyTransaction(id: Long)
}
