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
