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
