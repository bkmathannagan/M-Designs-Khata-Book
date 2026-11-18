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
