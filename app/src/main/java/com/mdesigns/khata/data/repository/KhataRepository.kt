package com.mdesigns.khata.data.repository

import com.mdesigns.khata.core.utils.KhataBalanceCalculator
import com.mdesigns.khata.data.dao.LedgerPersonDao
import com.mdesigns.khata.data.dao.TransactionDao
import com.mdesigns.khata.data.entity.LedgerPersonEntity
import com.mdesigns.khata.data.entity.TransactionEntity
import com.mdesigns.khata.domain.model.LedgerEntry
import com.mdesigns.khata.domain.model.PersonSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KhataRepository @Inject constructor(
    private val personDao: LedgerPersonDao,
    private val transactionDao: TransactionDao
) {
    fun getPeopleSummaries(profileId: Long): Flow<List<PersonSummary>> {
        return combine(
            personDao.getActivePeople(profileId),
            transactionDao.getAllTransactionsForProfile(profileId)
        ) { people, transactions ->
            val txByPerson = transactions.groupBy { it.personId }
            people.map { person ->
                val personTxs = txByPerson[person.id] ?: emptyList()
                val totalDebit = personTxs.filter { it.type == "DEBIT" }.sumOf { it.amount }
                val totalCredit = personTxs.filter { it.type == "CREDIT" }.sumOf { it.amount }

                val calc = KhataBalanceCalculator.calculate(
                    openingBalance = person.openingBalance,
                    isOpeningBalanceDebit = person.isOpeningBalanceDebit,
                    totalDebit = totalDebit,
                    totalCredit = totalCredit
                )

                val lastTxDate = personTxs.maxByOrNull { it.date }?.date

                PersonSummary(
                    person = person,
                    totalDebit = totalDebit,
                    totalCredit = totalCredit,
                    netBalance = calc.netBalance,
                    status = calc.status,
                    lastTransactionDate = lastTxDate
                )
            }
        }
    }

    fun getPersonLedger(personId: Long, openingBalance: Double, isOpeningDebit: Boolean): Flow<List<LedgerEntry>> {
        return transactionDao.getTransactionsForPerson(personId).map { transactions ->
            var currentBalance = if (isOpeningDebit) openingBalance else -openingBalance
            val entries = mutableListOf<LedgerEntry>()
            for (tx in transactions) {
                if (tx.type == "DEBIT") {
                    currentBalance += tx.amount
                } else {
                    currentBalance -= tx.amount
                }
                entries.add(LedgerEntry(transaction = tx, runningBalance = currentBalance))
            }
            entries.reversed() // Most recent first for ledger list
        }
    }

    suspend fun getPersonById(id: Long): LedgerPersonEntity? = personDao.getPersonById(id)
    suspend fun insertPerson(person: LedgerPersonEntity): Long = personDao.insertPerson(person)
    suspend fun updatePerson(person: LedgerPersonEntity) = personDao.updatePerson(person)
    suspend fun softDeletePerson(id: Long) = personDao.softDeletePerson(id)
    suspend fun restorePerson(id: Long) = personDao.restorePerson(id)
    suspend fun permanentlyDeletePerson(id: Long) = personDao.permanentlyDeletePerson(id)
    fun getDeletedPeople(profileId: Long) = personDao.getDeletedPeople(profileId)

    suspend fun insertTransaction(transaction: TransactionEntity): Long = transactionDao.insertTransaction(transaction)
    suspend fun updateTransaction(transaction: TransactionEntity) = transactionDao.updateTransaction(transaction)
    suspend fun softDeleteTransaction(id: Long) = transactionDao.softDeleteTransaction(id)
    suspend fun restoreTransaction(id: Long) = transactionDao.restoreTransaction(id)
    suspend fun permanentlyDeleteTransaction(id: Long) = transactionDao.permanentlyDeleteTransaction(id)
    fun getAllTransactions(profileId: Long): Flow<List<TransactionEntity>> = transactionDao.getAllTransactionsForProfile(profileId)
    fun getRecentTransactions(profileId: Long, limit: Int = 10) = transactionDao.getRecentTransactions(profileId, limit)
    fun getDeletedTransactions(profileId: Long) = transactionDao.getDeletedTransactions(profileId)

    suspend fun convertTransactionType(id: Long) {
        val tx = transactionDao.getTransactionById(id) ?: return
        val newType = if (tx.type == "DEBIT") "CREDIT" else "DEBIT"
        transactionDao.updateTransaction(tx.copy(type = newType, updatedAt = System.currentTimeMillis()))
    }

    suspend fun duplicateTransaction(id: Long) {
        val tx = transactionDao.getTransactionById(id) ?: return
        transactionDao.insertTransaction(tx.copy(id = 0, date = System.currentTimeMillis(), createdAt = System.currentTimeMillis()))
    }

    suspend fun moveTransaction(id: Long, targetPersonId: Long) {
        transactionDao.moveTransactionToPerson(id, targetPersonId)
    }
}
