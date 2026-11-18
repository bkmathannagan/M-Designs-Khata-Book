from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/core/security/CryptoUtils.kt', '''
package com.mdesigns.khata.core.security

import java.security.MessageDigest

object CryptoUtils {
    private const val SALT = "M_DESIGNS_KHATA_SECURE_SALT_2026"

    fun hashPin(pin: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest((pin + SALT).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPin(inputPin: String, storedHash: String): Boolean {
        return hashPin(inputPin) == storedHash
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/core/security/AppLockManager.kt', '''
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
''')

w('app/src/main/java/com/mdesigns/khata/core/security/BiometricHelper.kt', '''
package com.mdesigns.khata.core.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricHelper {
    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        title: String = "M Designs Khata Unlock",
        subtitle: String = "Authenticate to access your ledger",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("Authentication failed")
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Use PIN")
            .build()

        prompt.authenticate(promptInfo)
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/core/datastore/UserPreferencesDataStore.kt', '''
package com.mdesigns.khata.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val ACTIVE_PROFILE_ID = longPreferencesKey("active_profile_id")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val APP_LOCK_TYPE = stringPreferencesKey("app_lock_type")
        val APP_LOCK_PIN_HASH = stringPreferencesKey("app_lock_pin_hash")
        val APP_LOCK_TIMEOUT_MINUTES = intPreferencesKey("app_lock_timeout_minutes")
        val WHATSAPP_MESSAGE_TEMPLATE = stringPreferencesKey("whatsapp_message_template")
        val PREFER_WHATSAPP_BUSINESS = booleanPreferencesKey("prefer_whatsapp_business")
        val WEEKLY_EMAIL_ENABLED = booleanPreferencesKey("weekly_email_enabled")
        val WEEKLY_EMAIL_ADDRESS = stringPreferencesKey("weekly_email_address")
    }

    val isOnboarded: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.IS_ONBOARDED] ?: false }
    val activeProfileId: Flow<Long> = context.dataStore.data.map { it[PreferencesKeys.ACTIVE_PROFILE_ID] ?: 1L }
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.DARK_MODE] ?: false }
    val isSoundEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.SOUND_ENABLED] ?: true }
    val isVibrationEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.VIBRATION_ENABLED] ?: true }
    val isAppLockEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.APP_LOCK_ENABLED] ?: false }
    val appLockType: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.APP_LOCK_TYPE] ?: "PIN" }
    val appLockPinHash: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.APP_LOCK_PIN_HASH] }
    val appLockTimeoutMinutes: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.APP_LOCK_TIMEOUT_MINUTES] ?: 0 }
    val whatsappTemplate: Flow<String> = context.dataStore.data.map {
        it[PreferencesKeys.WHATSAPP_MESSAGE_TEMPLATE]
            ?: "Dear {NAME},\\nYour balance with {BUSINESS} is ₹{BALANCE} ({DIRECTION}). Please verify.\\nThank you,\\n{BUSINESS}\\nhttps://mdesignsrmd.com"
    }
    val preferWhatsAppBusiness: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.PREFER_WHATSAPP_BUSINESS] ?: false }
    val weeklyEmailEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.WEEKLY_EMAIL_ENABLED] ?: false }
    val weeklyEmailAddress: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.WEEKLY_EMAIL_ADDRESS] ?: "" }

    suspend fun setOnboarded(onboarded: Boolean) = context.dataStore.edit { it[PreferencesKeys.IS_ONBOARDED] = onboarded }
    suspend fun setActiveProfileId(id: Long) = context.dataStore.edit { it[PreferencesKeys.ACTIVE_PROFILE_ID] = id }
    suspend fun setDarkMode(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.DARK_MODE] = enabled }
    suspend fun setSoundEnabled(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.SOUND_ENABLED] = enabled }
    suspend fun setVibrationEnabled(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.VIBRATION_ENABLED] = enabled }
    suspend fun setAppLockEnabled(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.APP_LOCK_ENABLED] = enabled }
    suspend fun setAppLockType(type: String) = context.dataStore.edit { it[PreferencesKeys.APP_LOCK_TYPE] = type }
    suspend fun setAppLockPinHash(hash: String) = context.dataStore.edit { it[PreferencesKeys.APP_LOCK_PIN_HASH] = hash }
    suspend fun setAppLockTimeout(minutes: Int) = context.dataStore.edit { it[PreferencesKeys.APP_LOCK_TIMEOUT_MINUTES] = minutes }
    suspend fun setWhatsappTemplate(template: String) = context.dataStore.edit { it[PreferencesKeys.WHATSAPP_MESSAGE_TEMPLATE] = template }
    suspend fun setPreferWhatsAppBusiness(prefer: Boolean) = context.dataStore.edit { it[PreferencesKeys.PREFER_WHATSAPP_BUSINESS] = prefer }
    suspend fun setWeeklyEmailEnabled(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.WEEKLY_EMAIL_ENABLED] = enabled }
    suspend fun setWeeklyEmailAddress(address: String) = context.dataStore.edit { it[PreferencesKeys.WEEKLY_EMAIL_ADDRESS] = address }
}
''')
print("Security & DataStore generated")
