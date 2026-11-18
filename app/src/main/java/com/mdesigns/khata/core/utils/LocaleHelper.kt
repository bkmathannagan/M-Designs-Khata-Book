package com.mdesigns.khata.core.utils

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LocaleHelper {

    data class SupportedLanguage(
        val code: String,
        val nativeName: String,
        val englishName: String
    )

    val supportedLanguages = listOf(
        SupportedLanguage("en", "English", "English"),
        SupportedLanguage("hi", "हिन्दी", "Hindi"),
        SupportedLanguage("ta", "தமிழ்", "Tamil"),
        SupportedLanguage("te", "తెలుగు", "Telugu"),
        SupportedLanguage("ml", "മലയാളം", "Malayalam"),
        SupportedLanguage("kn", "ಕನ್ನಡ", "Kannada"),
        SupportedLanguage("gu", "ગુજરાતી", "Gujarati"),
        SupportedLanguage("mr", "मराठी", "Marathi"),
        SupportedLanguage("bn", "বাংলা", "Bengali")
    )

    @Suppress("DEPRECATION")
    fun setLocale(context: Context, languageCode: String) {
        val targetLocale = if (languageCode.isEmpty() || languageCode == "system") {
            Locale.getDefault()
        } else {
            Locale.forLanguageTag(languageCode)
        }

        val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }

        // Avoid infinite loop / recreation if the locale is already active
        if (currentLocale?.language == targetLocale.language) {
            return
        }

        Locale.setDefault(targetLocale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as? LocaleManager
            if (languageCode.isEmpty() || languageCode == "system") {
                localeManager?.applicationLocales = LocaleList.getEmptyLocaleList()
            } else {
                localeManager?.applicationLocales = LocaleList(targetLocale)
            }
        }

        val resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(targetLocale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun getLanguageName(code: String): String {
        return supportedLanguages.find { it.code == code }?.nativeName ?: "English"
    }
}
