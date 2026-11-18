from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/core/sharing/ShareManager.kt', '''
package com.mdesigns.khata.core.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.net.URLEncoder

object ShareManager {

    fun shareToWhatsApp(
        context: Context,
        phoneNumber: String?,
        message: String,
        preferBusiness: Boolean = false
    ) {
        val cleanPhone = phoneNumber?.replace("+", "")?.replace(" ", "")?.replace("-", "") ?: ""
        val url = if (cleanPhone.isNotEmpty()) {
            "https://api.whatsapp.com/send?phone=$cleanPhone&text=${URLEncoder.encode(message, "UTF-8")}"
        } else {
            "https://api.whatsapp.com/send?text=${URLEncoder.encode(message, "UTF-8")}"
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            if (preferBusiness) {
                `package` = "com.whatsapp.w4b"
            }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(fallbackIntent, "Share statement via"))
        }
    }

    fun sendSms(context: Context, phoneNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${phoneNumber.trim()}")
            putExtra("sms_body", message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(fallbackIntent, "Send SMS"))
        }
    }

    fun shareFile(context: Context, file: File, mimeType: String, title: String = "Share Document") {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(Intent.createChooser(intent, title))
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/core/reminders/NotificationHelper.kt', '''
package com.mdesigns.khata.core.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mdesigns.khata.MainActivity
import com.mdesigns.khata.R

object NotificationHelper {
    const val CHANNEL_REMINDERS = "channel_payment_reminders"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Payment Reminders"
            val descriptionText = "Notifications for payment due dates and reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_REMINDERS, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showPaymentReminder(
        context: Context,
        id: Int,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, builder.build())
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/core/reminders/ReminderWorker.kt', '''
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
''')
print("Sharing & Reminders generated")
