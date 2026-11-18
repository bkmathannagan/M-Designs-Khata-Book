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
