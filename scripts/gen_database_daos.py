from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/data/dao/BusinessProfileDao.kt', '''
package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.BusinessProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessProfileDao {
    @Query("SELECT * FROM business_profiles ORDER BY id ASC")
    fun getAllProfiles(): Flow<List<BusinessProfileEntity>>

    @Query("SELECT * FROM business_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): BusinessProfileEntity?

    @Query("SELECT * FROM business_profiles WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultProfile(): BusinessProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: BusinessProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: BusinessProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: BusinessProfileEntity)
}
''')

w('app/src/main/java/com/mdesigns/khata/data/dao/LedgerPersonDao.kt', '''
package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.LedgerPersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerPersonDao {
    @Query("SELECT * FROM ledger_people WHERE profileId = :profileId AND isDeleted = 0 ORDER BY name ASC")
    fun getActivePeople(profileId: Long): Flow<List<LedgerPersonEntity>>

    @Query("SELECT * FROM ledger_people WHERE profileId = :profileId AND isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedPeople(profileId: Long): Flow<List<LedgerPersonEntity>>

    @Query("SELECT * FROM ledger_people WHERE id = :id LIMIT 1")
    suspend fun getPersonById(id: Long): LedgerPersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: LedgerPersonEntity): Long

    @Update
    suspend fun updatePerson(person: LedgerPersonEntity)

    @Query("UPDATE ledger_people SET isDeleted = 1, deletedAt = :timestamp WHERE id = :id")
    suspend fun softDeletePerson(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE ledger_people SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restorePerson(id: Long)

    @Query("DELETE FROM ledger_people WHERE id = :id")
    suspend fun permanentlyDeletePerson(id: Long)

    @Query("UPDATE ledger_people SET isSettled = :isSettled WHERE id = :id")
    suspend fun updateSettledStatus(id: Long, isSettled: Boolean)
}
''')

w('app/src/main/java/com/mdesigns/khata/data/dao/TransactionDao.kt', '''
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
''')

w('app/src/main/java/com/mdesigns/khata/data/dao/MoneyTransactionDao.kt', '''
package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.MoneyTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoneyTransactionDao {
    @Query("SELECT * FROM money_transactions WHERE profileId = :profileId AND isDeleted = 0 ORDER BY date DESC")
    fun getAllMoneyTransactions(profileId: Long): Flow<List<MoneyTransactionEntity>>

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
''')

w('app/src/main/java/com/mdesigns/khata/data/dao/CategoryDao.kt', '''
package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE profileId = :profileId AND isActive = 1 ORDER BY sortOrder ASC")
    fun getCategories(profileId: Long): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}
''')

w('app/src/main/java/com/mdesigns/khata/data/dao/ReminderDao.kt', '''
package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE profileId = :profileId AND status = 'PENDING' ORDER BY reminderDateTime ASC")
    fun getPendingReminders(profileId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE profileId = :profileId ORDER BY reminderDateTime DESC")
    fun getAllReminders(profileId: Long): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}
''')

w('app/src/main/java/com/mdesigns/khata/data/dao/AttachmentDao.kt', '''
package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.AttachmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {
    @Query("SELECT * FROM attachments WHERE transactionId = :transactionId")
    fun getAttachmentsForTransaction(transactionId: Long): Flow<List<AttachmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: AttachmentEntity): Long

    @Delete
    suspend fun deleteAttachment(attachment: AttachmentEntity)
}
''')

w('app/src/main/java/com/mdesigns/khata/data/dao/BackupRecordDao.kt', '''
package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.BackupRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupRecordDao {
    @Query("SELECT * FROM backup_records WHERE profileId = :profileId ORDER BY backupDate DESC")
    fun getBackupRecords(profileId: Long): Flow<List<BackupRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackupRecord(record: BackupRecordEntity): Long
}
''')

w('app/src/main/java/com/mdesigns/khata/core/database/KhataDatabase.kt', '''
package com.mdesigns.khata.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mdesigns.khata.data.dao.*
import com.mdesigns.khata.data.entity.*

@Database(
    entities = [
        BusinessProfileEntity::class,
        LedgerPersonEntity::class,
        TransactionEntity::class,
        MoneyTransactionEntity::class,
        CategoryEntity::class,
        ReminderEntity::class,
        AttachmentEntity::class,
        BackupRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class KhataDatabase : RoomDatabase() {
    abstract fun businessProfileDao(): BusinessProfileDao
    abstract fun ledgerPersonDao(): LedgerPersonDao
    abstract fun transactionDao(): TransactionDao
    abstract fun moneyTransactionDao(): MoneyTransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun reminderDao(): ReminderDao
    abstract fun attachmentDao(): AttachmentDao
    abstract fun backupRecordDao(): BackupRecordDao
}
''')
print("DAOs and Room Database generated")
