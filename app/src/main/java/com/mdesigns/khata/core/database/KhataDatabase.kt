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
