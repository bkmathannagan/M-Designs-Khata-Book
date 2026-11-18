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
