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
