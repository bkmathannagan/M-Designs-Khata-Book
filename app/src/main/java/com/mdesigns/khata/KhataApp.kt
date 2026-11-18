package com.mdesigns.khata

import android.app.Application
import com.mdesigns.khata.core.reminders.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KhataApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
    }
}
