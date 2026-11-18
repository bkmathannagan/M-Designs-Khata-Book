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
        val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
    }

    val isOnboarded: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.IS_ONBOARDED] ?: false }
    val activeProfileId: Flow<Long> = context.dataStore.data.map { it[PreferencesKeys.ACTIVE_PROFILE_ID] ?: 1L }
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.DARK_MODE] ?: false }
    val selectedLanguage: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.SELECTED_LANGUAGE] ?: "en" }
    val isSoundEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.SOUND_ENABLED] ?: true }
    val isVibrationEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.VIBRATION_ENABLED] ?: true }
    val isAppLockEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.APP_LOCK_ENABLED] ?: false }
    val appLockType: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.APP_LOCK_TYPE] ?: "PIN" }
    val appLockPinHash: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.APP_LOCK_PIN_HASH] }
    val appLockTimeoutMinutes: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.APP_LOCK_TIMEOUT_MINUTES] ?: 0 }
    val whatsappTemplate: Flow<String> = context.dataStore.data.map {
        it[PreferencesKeys.WHATSAPP_MESSAGE_TEMPLATE]
            ?: "Dear {NAME},\nYour balance with {BUSINESS} is ₹{BALANCE} ({DIRECTION}). Please verify.\nThank you,\n{BUSINESS}\nhttps://mdesignsrmd.com"
    }
    val preferWhatsAppBusiness: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.PREFER_WHATSAPP_BUSINESS] ?: false }
    val weeklyEmailEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.WEEKLY_EMAIL_ENABLED] ?: false }
    val weeklyEmailAddress: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.WEEKLY_EMAIL_ADDRESS] ?: "" }

    suspend fun setOnboarded(onboarded: Boolean) = context.dataStore.edit { it[PreferencesKeys.IS_ONBOARDED] = onboarded }
    suspend fun setActiveProfileId(id: Long) = context.dataStore.edit { it[PreferencesKeys.ACTIVE_PROFILE_ID] = id }
    suspend fun setDarkMode(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.DARK_MODE] = enabled }
    suspend fun setSelectedLanguage(lang: String) = context.dataStore.edit { it[PreferencesKeys.SELECTED_LANGUAGE] = lang }
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
