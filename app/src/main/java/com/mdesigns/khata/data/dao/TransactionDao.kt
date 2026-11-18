package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE personId = :personId AND isDeleted = 0 ORDER BY date ASC, id ASC")
    fun getTransactionsForPerson(personId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE profileId = :profileId AND isDeleted = 0 ORDER BY date DESC, id DESC")
    fun getAllTransactionsForProfile(profileId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE profileId = :profileId")
    suspend fun getAllTransactionsList(profileId: Long): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE profileId = :profileId AND isDeleted = 0 ORDER BY date DESC, id DESC LIMIT :limit")
    fun getRecentTransactions(profileId: Long, limit: Int = 10): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE profileId = :profileId AND isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedTransactions(profileId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE personId = :personId AND type = 'DEBIT' AND isDeleted = 0")
    suspend fun getTotalDebitForPerson(personId: Long): Double

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE personId = :personId AND type = 'CREDIT' AND isDeleted = 0")
    suspend fun getTotalCreditForPerson(personId: Long): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("UPDATE transactions SET isDeleted = 1, deletedAt = :timestamp WHERE id = :id")
    suspend fun softDeleteTransaction(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE transactions SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restoreTransaction(id: Long)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun permanentlyDeleteTransaction(id: Long)

    @Query("UPDATE transactions SET personId = :newPersonId WHERE id = :id")
    suspend fun moveTransactionToPerson(id: Long, newPersonId: Long)
}
