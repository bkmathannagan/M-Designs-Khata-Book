from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/KhataApp.kt', '''
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
''')

w('app/src/main/java/com/mdesigns/khata/core/di/DatabaseModule.kt', '''
package com.mdesigns.khata.core.di

import android.content.Context
import androidx.room.Room
import com.mdesigns.khata.core.database.KhataDatabase
import com.mdesigns.khata.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideKhataDatabase(@ApplicationContext context: Context): KhataDatabase {
        return Room.databaseBuilder(
            context,
            KhataDatabase::class.java,
            "khata_database.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideBusinessProfileDao(db: KhataDatabase): BusinessProfileDao = db.businessProfileDao()

    @Provides
    fun provideLedgerPersonDao(db: KhataDatabase): LedgerPersonDao = db.ledgerPersonDao()

    @Provides
    fun provideTransactionDao(db: KhataDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideMoneyTransactionDao(db: KhataDatabase): MoneyTransactionDao = db.moneyTransactionDao()

    @Provides
    fun provideCategoryDao(db: KhataDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideReminderDao(db: KhataDatabase): ReminderDao = db.reminderDao()

    @Provides
    fun provideAttachmentDao(db: KhataDatabase): AttachmentDao = db.attachmentDao()

    @Provides
    fun provideBackupRecordDao(db: KhataDatabase): BackupRecordDao = db.backupRecordDao()
}
''')

w('app/src/main/java/com/mdesigns/khata/navigation/KhataNavGraph.kt', '''
package com.mdesigns.khata.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.core.security.AppLockManager
import com.mdesigns.khata.feature.auth.LockScreen
import com.mdesigns.khata.feature.dashboard.HomeScreen
import com.mdesigns.khata.feature.khata.KhataScreen
import com.mdesigns.khata.feature.ledger.PersonLedgerScreen
import com.mdesigns.khata.feature.money.MoneyScreen
import com.mdesigns.khata.feature.more.MoreScreen
import com.mdesigns.khata.feature.more.about.AboutScreen
import com.mdesigns.khata.feature.more.backup.BackupRestoreScreen
import com.mdesigns.khata.feature.more.businesscard.BusinessCardScreen
import com.mdesigns.khata.feature.more.denomination.DenominationScreen
import com.mdesigns.khata.feature.more.holidays.BankHolidaysScreen
import com.mdesigns.khata.feature.more.letterhead.LetterheadScreen
import com.mdesigns.khata.feature.more.profile.BusinessProfileScreen
import com.mdesigns.khata.feature.more.recyclebin.RecycleBinScreen
import com.mdesigns.khata.feature.more.reminders.RemindersScreen
import com.mdesigns.khata.feature.more.settings.SettingsScreen
import com.mdesigns.khata.feature.onboarding.OnboardingScreen
import com.mdesigns.khata.feature.person.AddEditPersonScreen
import com.mdesigns.khata.feature.splash.SplashScreen

@Composable
fun KhataNavGraph(
    navController: NavHostController,
    preferences: UserPreferencesDataStore,
    appLockManager: AppLockManager,
    modifier: Modifier = Modifier,
    onBiometricAuth: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                },
                onNavigateToLock = {
                    navController.navigate(Screen.Lock.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                preferences = preferences,
                onFinished = {
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } }
                }
            )
        }

        composable(Screen.Lock.route) {
            LockScreen(
                storedPinHash = null,
                appLockManager = appLockManager,
                onUnlocked = {
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Lock.route) { inclusive = true } }
                },
                onBiometricClick = onBiometricAuth
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToKhata = { navController.navigate(Screen.Khata.route) },
                onNavigateToMoney = { navController.navigate(Screen.Money.route) },
                onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                onNavigateToAddPerson = { navController.navigate(Screen.AddPerson.createRoute()) },
                onNavigateToPersonLedger = { personId -> navController.navigate(Screen.PersonLedger.createRoute(personId)) }
            )
        }

        composable(Screen.Khata.route) {
            KhataScreen(
                onNavigateToAddPerson = { navController.navigate(Screen.AddPerson.createRoute()) },
                onNavigateToPersonLedger = { personId -> navController.navigate(Screen.PersonLedger.createRoute(personId)) }
            )
        }

        composable(Screen.Reports.route) {
            com.mdesigns.khata.feature.reports.ReportsScreen()
        }

        composable(Screen.Money.route) {
            MoneyScreen()
        }

        composable(Screen.More.route) {
            MoreScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable(
            route = Screen.AddPerson.route,
            arguments = listOf(navArgument("personId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val personId = backStackEntry.arguments?.getString("personId")?.toLongOrNull()
            AddEditPersonScreen(
                personId = personId,
                onNavigateBack = { navController.popBackStack() },
                onPersonSaved = { newId ->
                    navController.popBackStack()
                    navController.navigate(Screen.PersonLedger.createRoute(newId))
                }
            )
        }

        composable(
            route = Screen.PersonLedger.route,
            arguments = listOf(navArgument("personId") { type = NavType.StringType })
        ) {
            PersonLedgerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.BusinessProfile.route) {
            BusinessProfileScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.BusinessCard.route) {
            BusinessCardScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Letterhead.route) {
            LetterheadScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.RecycleBin.route) {
            RecycleBinScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Reminders.route) {
            RemindersScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() }, preferences = preferences)
        }

        composable(Screen.BackupRestore.route) {
            BackupRestoreScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Denomination.route) {
            DenominationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.BankHolidays.route) {
            BankHolidaysScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.About.route) {
            AboutScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/MainActivity.kt', '''
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
''')
print("Navigation & App Entrypoint generated")
