from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/domain/model/DomainModels.kt', '''
package com.mdesigns.khata.domain.model

import com.mdesigns.khata.core.utils.KhataBalanceCalculator
import com.mdesigns.khata.data.entity.LedgerPersonEntity
import com.mdesigns.khata.data.entity.TransactionEntity

data class PersonSummary(
    val person: LedgerPersonEntity,
    val totalDebit: Double,
    val totalCredit: Double,
    val netBalance: Double,
    val status: KhataBalanceCalculator.BalanceStatus,
    val lastTransactionDate: Long? = null
)

data class LedgerEntry(
    val transaction: TransactionEntity,
    val runningBalance: Double
)

data class DashboardSummary(
    val totalReceivable: Double = 0.0, // You Collect
    val totalPayable: Double = 0.0,    // You Give
    val netBalance: Double = 0.0,
    val todayIncome: Double = 0.0,
    val todayExpense: Double = 0.0,
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val customerCount: Int = 0,
    val supplierCount: Int = 0,
    val settledCount: Int = 0,
    val pendingCount: Int = 0
)

enum class PersonFilter {
    ALL,
    RECEIVABLE, // You Collect
    PAYABLE,    // You Give
    SETTLED,    // Zero Balance
    CUSTOMERS,
    SUPPLIERS
}
''')

w('app/src/main/java/com/mdesigns/khata/data/repository/KhataRepository.kt', '''
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
''')

w('app/src/main/java/com/mdesigns/khata/data/repository/MoneyRepository.kt', '''
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

    fun getCategories(profileId: Long): Flow<List<CategoryEntity>> =
        categoryDao.getCategories(profileId)

    suspend fun insertMoneyTransaction(transaction: MoneyTransactionEntity): Long =
        moneyDao.insertMoneyTransaction(transaction)

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
''')

w('app/src/main/java/com/mdesigns/khata/data/repository/ProfileRepository.kt', '''
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
''')

w('app/src/main/java/com/mdesigns/khata/data/repository/BackupRepository.kt', '''
package com.mdesigns.khata.data.repository

import com.mdesigns.khata.data.dao.BackupRecordDao
import com.mdesigns.khata.data.entity.BackupRecordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepository @Inject constructor(
    private val backupDao: BackupRecordDao
) {
    fun getBackupRecords(profileId: Long): Flow<List<BackupRecordEntity>> =
        backupDao.getBackupRecords(profileId)

    suspend fun insertBackupRecord(record: BackupRecordEntity): Long =
        backupDao.insertBackupRecord(record)
}
''')
print("Repositories & Domain Models generated")
