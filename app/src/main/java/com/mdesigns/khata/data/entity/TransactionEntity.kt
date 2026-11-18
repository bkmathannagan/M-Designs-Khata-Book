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
