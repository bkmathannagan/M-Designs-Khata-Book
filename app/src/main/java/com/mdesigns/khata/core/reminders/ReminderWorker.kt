package com.mdesigns.khata.core.reminders

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: "Payment Reminder"
        val message = inputData.getString("message") ?: "You have a pending khata balance reminder."
        val reminderId = inputData.getInt("reminder_id", 101)

        NotificationHelper.showPaymentReminder(context, reminderId, title, message)
        return Result.success()
    }
}
