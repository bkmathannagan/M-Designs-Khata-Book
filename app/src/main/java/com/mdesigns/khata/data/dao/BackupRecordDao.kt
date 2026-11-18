package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.BackupRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupRecordDao {
    @Query("SELECT * FROM backup_records WHERE profileId = :profileId ORDER BY backupDate DESC")
    fun getBackupRecords(profileId: Long): Flow<List<BackupRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackupRecord(record: BackupRecordEntity): Long
}
