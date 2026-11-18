package com.mdesigns.khata.core.security

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLockManager @Inject constructor() {
    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private var lastBackgroundTimestamp: Long = 0

    fun unlock() {
        _isUnlocked.value = true
    }

    fun lock() {
        _isUnlocked.value = false
    }

    fun onAppBackgrounded() {
        lastBackgroundTimestamp = System.currentTimeMillis()
    }

    fun onAppForegrounded(timeoutMinutes: Int, isLockEnabled: Boolean) {
        if (!isLockEnabled) {
            _isUnlocked.value = true
            return
        }
        if (timeoutMinutes == 0) {
            _isUnlocked.value = false
        } else {
            val elapsed = System.currentTimeMillis() - lastBackgroundTimestamp
            val timeoutMs = timeoutMinutes * 60 * 1000L
            if (elapsed > timeoutMs) {
                _isUnlocked.value = false
            }
        }
    }
}
