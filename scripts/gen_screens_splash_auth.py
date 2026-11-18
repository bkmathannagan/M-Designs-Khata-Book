from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/feature/splash/SplashViewModel.kt', '''
package com.mdesigns.khata.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.data.entity.BusinessProfileEntity
import com.mdesigns.khata.data.entity.CategoryEntity
import com.mdesigns.khata.data.repository.MoneyRepository
import com.mdesigns.khata.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashDestination {
    object Loading : SplashDestination
    object Onboarding : SplashDestination
    object Lock : SplashDestination
    object Home : SplashDestination
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferences: UserPreferencesDataStore,
    private val profileRepository: ProfileRepository,
    private val moneyRepository: MoneyRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            seedInitialDataIfNeeded()
        }
    }

    val destination = combine(
        preferences.isOnboarded,
        preferences.isAppLockEnabled
    ) { isOnboarded, isAppLockEnabled ->
        when {
            !isOnboarded -> SplashDestination.Onboarding
            isAppLockEnabled -> SplashDestination.Lock
            else -> SplashDestination.Home
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SplashDestination.Loading)

    private suspend fun seedInitialDataIfNeeded() {
        val defaultProfile = profileRepository.getDefaultProfile()
        if (defaultProfile == null) {
            val profileId = profileRepository.insertProfile(
                BusinessProfileEntity(
                    name = "My Shop / Business",
                    ownerName = "Proprietor",
                    website = "https://mdesignsrmd.com",
                    isDefault = true
                )
            )
            preferences.setActiveProfileId(profileId)

            val initialCategories = listOf(
                CategoryEntity(profileId = profileId, name = "Sales", type = "INCOME", sortOrder = 1),
                CategoryEntity(profileId = profileId, name = "Services", type = "INCOME", sortOrder = 2),
                CategoryEntity(profileId = profileId, name = "Rent", type = "EXPENSE", sortOrder = 3),
                CategoryEntity(profileId = profileId, name = "Salary", type = "EXPENSE", sortOrder = 4),
                CategoryEntity(profileId = profileId, name = "Electricity", type = "EXPENSE", sortOrder = 5),
                CategoryEntity(profileId = profileId, name = "Purchase", type = "EXPENSE", sortOrder = 6)
            )
            for (cat in initialCategories) {
                moneyRepository.insertCategory(cat)
            }
        }
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/splash/SplashScreen.kt', '''
package com.mdesigns.khata.feature.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mdesigns.khata.core.theme.PrimaryTeal
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToLock: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsState()

    var startAnimation by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.6f,
        animationSpec = tween(durationMillis = 800, easing = OvershootInterpolatorEasing),
        label = "splash_scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1200)
    }

    LaunchedEffect(destination) {
        if (startAnimation) {
            delay(800)
            when (destination) {
                SplashDestination.Onboarding -> onNavigateToOnboarding()
                SplashDestination.Lock -> onNavigateToLock()
                SplashDestination.Home -> onNavigateToHome()
                SplashDestination.Loading -> Unit
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryTeal),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value)
        ) {
            Surface(
                modifier = Modifier.size(90.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "M Designs Khata",
                        tint = PrimaryTeal,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "M Designs Khata",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Credit • Debit • Udhar • Ledger",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "https://mdesignsrmd.com",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.65f)
            )
        }
    }
}

private val OvershootInterpolatorEasing = Easing { fraction ->
    val tension = 2.0f
    val t = fraction - 1.0f
    t * t * ((tension + 1) * t + tension) + 1.0f
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/onboarding/OnboardingScreen.kt', '''
package com.mdesigns.khata.feature.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.core.theme.PrimaryTeal
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    preferences: UserPreferencesDataStore
) {
    val scope = rememberCoroutineScope()
    var currentPage by remember { mutableStateOf(0) }

    val pages = listOf(
        OnboardingPage(
            title = "Your Digital Khata",
            description = "Replace traditional paper bahikhata. Easily manage customers, suppliers, and personal credit & debit ledgers.",
            icon = Icons.Default.AccountBalanceWallet
        ),
        OnboardingPage(
            title = "Give & Receive Easily",
            description = "Record 'Debit / You Give' and 'Credit / You Receive' transactions in seconds with automated running balance.",
            icon = Icons.Default.SwapHoriz
        ),
        OnboardingPage(
            title = "Reports & WhatsApp Sharing",
            description = "Generate professional PDF statements and Excel sheets with your own letterhead. Send instant WhatsApp payment reminders.",
            icon = Icons.Default.PictureAsPdf
        ),
        OnboardingPage(
            title = "Private, Fast & Secure",
            description = "100% offline-first. Keep your financial records secure with PIN/Biometric lock and Google Drive cloud backup.",
            icon = Icons.Default.Security
        )
    )

    fun completeOnboarding() {
        scope.launch {
            preferences.setOnboarded(true)
            onFinished()
        }
    }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentPage < pages.size - 1) {
                    TextButton(onClick = { completeOnboarding() }) {
                        Text("Skip", color = MaterialTheme.colorScheme.outline)
                    }
                    Button(
                        onClick = { currentPage++ },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Next")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                    Button(
                        onClick = { completeOnboarding() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Get Started")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val page = pages[currentPage]

            Surface(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(32.dp),
                color = PrimaryTeal.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Page Indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentPage) 18.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(if (index == currentPage) PrimaryTeal else MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }
        }
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/auth/LockScreen.kt', '''
package com.mdesigns.khata.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdesigns.khata.core.security.AppLockManager
import com.mdesigns.khata.core.security.CryptoUtils
import com.mdesigns.khata.core.theme.PrimaryTeal

@Composable
fun LockScreen(
    storedPinHash: String?,
    appLockManager: AppLockManager,
    onUnlocked: () -> Unit,
    onBiometricClick: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun handleKey(key: String) {
        errorMessage = null
        if (enteredPin.length < 4) {
            enteredPin += key
            if (enteredPin.length == 4) {
                if (storedPinHash == null || CryptoUtils.verifyPin(enteredPin, storedPinHash)) {
                    appLockManager.unlock()
                    onUnlocked()
                } else {
                    errorMessage = "Incorrect PIN. Please try again."
                    enteredPin = ""
                }
            }
        }
    }

    fun handleDelete() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            errorMessage = null
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock",
                    tint = PrimaryTeal,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Enter Khata PIN",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "M Designs Khata is protected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))

                // PIN Dots
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    for (i in 0 until 4) {
                        val filled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (filled) PrimaryTeal else MaterialTheme.colorScheme.outlineVariant)
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Keypad
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("BIO", "0", "DEL")
                )

                for (row in keys) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (key in row) {
                            when (key) {
                                "BIO" -> {
                                    IconButton(
                                        onClick = { onBiometricClick() },
                                        modifier = Modifier.size(72.dp)
                                    ) {
                                        Icon(Icons.Default.Fingerprint, contentDescription = "Biometric", tint = PrimaryTeal, modifier = Modifier.size(32.dp))
                                    }
                                }
                                "DEL" -> {
                                    IconButton(
                                        onClick = { handleDelete() },
                                        modifier = Modifier.size(72.dp)
                                    ) {
                                        Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                                else -> {
                                    Surface(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .clickable { handleKey(key) },
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = key, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
''')
print("Splash & Auth screens generated")
