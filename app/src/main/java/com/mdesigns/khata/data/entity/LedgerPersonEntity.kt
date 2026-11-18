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
