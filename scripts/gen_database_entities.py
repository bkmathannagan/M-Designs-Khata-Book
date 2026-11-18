from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/data/entity/BusinessProfileEntity.kt', '''
package com.mdesigns.khata.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profiles")
data class BusinessProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val ownerName: String = "",
    val mobile: String = "",
    val alternateMobile: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val email: String = "",
    val website: String = "https://mdesignsrmd.com",
    val gstin: String = "",
    val logoUri: String? = null,
    val qrUri: String? = null,
    val stampUri: String? = null,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
''')

w('app/src/main/java/com/mdesigns/khata/data/entity/LedgerPersonEntity.kt', '''
package com.mdesigns.khata.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ledger_people",
    indices = [
        Index("profileId"),
        Index("name"),
        Index("mobile"),
        Index("isDeleted")
    ]
)
data class LedgerPersonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val name: String,
    val mobile: String = "",
    val alternateMobile: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val email: String = "",
    val category: String = "Customer", // Customer, Supplier, Personal
    val openingBalance: Double = 0.0,
    val isOpeningBalanceDebit: Boolean = true, // true = You Collect (Debit), false = You Give (Credit)
    val notes: String = "",
    val photoUri: String? = null,
    val isSettled: Boolean = false,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
''')

w('app/src/main/java/com/mdesigns/khata/data/entity/TransactionEntity.kt', '''
package com.mdesigns.khata.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index("profileId"),
        Index("personId"),
        Index("date"),
        Index("isDeleted")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val personId: Long,
    val type: String, // DEBIT (You Give) / CREDIT (You Receive)
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val description: String = "",
    val categoryId: Long? = null,
    val paymentMode: String = "Cash", // Cash, UPI, Bank Transfer, Cheque, Card, Other
    val dueDate: Long? = null,
    val attachmentUri: String? = null,
    val referenceNumber: String = "",
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
''')

w('app/src/main/java/com/mdesigns/khata/data/entity/MoneyTransactionEntity.kt', '''
package com.mdesigns.khata.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "money_transactions",
    indices = [
        Index("profileId"),
        Index("date"),
        Index("type"),
        Index("isDeleted")
    ]
)
data class MoneyTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val type: String, // INCOME / EXPENSE
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val category: String = "General",
    val description: String = "",
    val paymentMode: String = "Cash",
    val attachmentUri: String? = null,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
''')

w('app/src/main/java/com/mdesigns/khata/data/entity/CategoryEntity.kt', '''
package com.mdesigns.khata.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val name: String,
    val type: String = "EXPENSE", // INCOME / EXPENSE / LEDGER
    val icon: String = "default",
    val sortOrder: Int = 0,
    val isActive: Boolean = true
)
''')

w('app/src/main/java/com/mdesigns/khata/data/entity/ReminderEntity.kt', '''
package com.mdesigns.khata.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    indices = [
        Index("personId"),
        Index("reminderDateTime"),
        Index("status")
    ]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val personId: Long,
    val transactionId: Long? = null,
    val amount: Double = 0.0,
    val dueDate: Long? = null,
    val reminderDateTime: Long,
    val recurrence: String = "ONCE", // ONCE, DAILY, WEEKLY, MONTHLY
    val message: String = "",
    val status: String = "PENDING", // PENDING, COMPLETED, CANCELLED
    val createdAt: Long = System.currentTimeMillis()
)
''')

w('app/src/main/java/com/mdesigns/khata/data/entity/AttachmentEntity.kt', '''
package com.mdesigns.khata.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attachments")
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val transactionId: Long? = null,
    val personId: Long? = null,
    val uri: String,
    val name: String = "",
    val mimeType: String = "image/jpeg",
    val size: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)
''')

w('app/src/main/java/com/mdesigns/khata/data/entity/BackupRecordEntity.kt', '''
package com.mdesigns.khata.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backup_records")
data class BackupRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val driveFileId: String = "",
    val backupDate: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS",
    val checksum: String = "",
    val version: Int = 1
)
''')

w('app/src/main/java/com/mdesigns/khata/core/database/Converters.kt', '''
package com.mdesigns.khata.core.database

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
''')

print("Database Entities & Converters generated")
