package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE profileId = :profileId AND status = 'PENDING' ORDER BY reminderDateTime ASC")
    fun getPendingReminders(profileId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE profileId = :profileId ORDER BY reminderDateTime DESC")
    fun getAllReminders(profileId: Long): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}
