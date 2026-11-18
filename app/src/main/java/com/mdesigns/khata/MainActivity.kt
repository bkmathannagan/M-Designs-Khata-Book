package com.mdesigns.khata

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.core.security.AppLockManager
import com.mdesigns.khata.core.security.BiometricHelper
import com.mdesigns.khata.core.theme.MDesignsKhataTheme
import com.mdesigns.khata.navigation.BottomNavigationBar
import com.mdesigns.khata.navigation.KhataNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var preferences: UserPreferencesDataStore

    @Inject
    lateinit var appLockManager: AppLockManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkMode by preferences.isDarkMode.collectAsState(initial = false)
            val selectedLanguage by preferences.selectedLanguage.collectAsState(initial = null)

            LaunchedEffect(selectedLanguage) {
                selectedLanguage?.let { lang ->
                    com.mdesigns.khata.core.utils.LocaleHelper.setLocale(this@MainActivity, lang)
                }
            }

            MDesignsKhataTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    Surface(modifier = Modifier.fillMaxSize()) {
                        KhataNavGraph(
                            navController = navController,
                            preferences = preferences,
                            appLockManager = appLockManager,
                            modifier = Modifier.padding(innerPadding),
                            onBiometricAuth = {
                                if (BiometricHelper.isBiometricAvailable(this@MainActivity)) {
                                    BiometricHelper.showBiometricPrompt(
                                        activity = this@MainActivity,
                                        onSuccess = { appLockManager.unlock() },
                                        onError = { /* fallback to pin */ }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
