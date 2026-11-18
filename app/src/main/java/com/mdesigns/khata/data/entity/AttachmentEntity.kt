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
