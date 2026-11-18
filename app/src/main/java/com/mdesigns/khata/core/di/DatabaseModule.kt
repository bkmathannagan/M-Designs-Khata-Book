package com.mdesigns.khata.core.di

import android.content.Context
import androidx.room.Room
import com.mdesigns.khata.core.database.KhataDatabase
import com.mdesigns.khata.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideKhataDatabase(@ApplicationContext context: Context): KhataDatabase {
        return Room.databaseBuilder(
            context,
            KhataDatabase::class.java,
            "khata_database.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideBusinessProfileDao(db: KhataDatabase): BusinessProfileDao = db.businessProfileDao()

    @Provides
    fun provideLedgerPersonDao(db: KhataDatabase): LedgerPersonDao = db.ledgerPersonDao()

    @Provides
    fun provideTransactionDao(db: KhataDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideMoneyTransactionDao(db: KhataDatabase): MoneyTransactionDao = db.moneyTransactionDao()

    @Provides
    fun provideCategoryDao(db: KhataDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideReminderDao(db: KhataDatabase): ReminderDao = db.reminderDao()

    @Provides
    fun provideAttachmentDao(db: KhataDatabase): AttachmentDao = db.attachmentDao()

    @Provides
    fun provideBackupRecordDao(db: KhataDatabase): BackupRecordDao = db.backupRecordDao()
}
