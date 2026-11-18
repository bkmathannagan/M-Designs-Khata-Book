package com.mdesigns.khata.feature.more.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.core.security.CryptoUtils
import com.mdesigns.khata.core.utils.LocaleHelper
import com.mdesigns.khata.core.theme.PrimaryTeal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    preferences: UserPreferencesDataStore
) {
    val scope = rememberCoroutineScope()
    val isAppLockEnabled by preferences.isAppLockEnabled.collectAsState(initial = false)
    val isSoundEnabled by preferences.isSoundEnabled.collectAsState(initial = true)
    val isVibrationEnabled by preferences.isVibrationEnabled.collectAsState(initial = true)
    val preferWhatsAppBusiness by preferences.preferWhatsAppBusiness.collectAsState(initial = false)
    val selectedLanguage by preferences.selectedLanguage.collectAsState(initial = "en")
    val weeklyEmailEnabled by preferences.weeklyEmailEnabled.collectAsState(initial = false)
    val weeklyEmailAddress by preferences.weeklyEmailAddress.collectAsState(initial = "")

    var showPinDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var emailInput by remember(weeklyEmailAddress) { mutableStateOf(weeklyEmailAddress) }
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Security") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Security & Privacy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryTeal)

            Card(shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("App PIN Lock", fontWeight = FontWeight.SemiBold)
                            Text("Protect ledger with 4-digit PIN", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = isAppLockEnabled,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    showPinDialog = true
                                } else {
                                    scope.launch { preferences.setAppLockEnabled(false) }
                                }
                            }
                        )
                    }
                }
            }

            Text("Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryTeal)

            Card(shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLanguageDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Language / भाषा / மொழி", fontWeight = FontWeight.Medium)
                            Text(
                                com.mdesigns.khata.core.utils.LocaleHelper.getLanguageName(selectedLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimaryTeal
                            )
                        }
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Select Language")
                    }
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sound Feedback", fontWeight = FontWeight.Medium)
                        Switch(checked = isSoundEnabled, onCheckedChange = { scope.launch { preferences.setSoundEnabled(it) } })
                    }
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Vibration", fontWeight = FontWeight.Medium)
                        Switch(checked = isVibrationEnabled, onCheckedChange = { scope.launch { preferences.setVibrationEnabled(it) } })
                    }
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("WhatsApp Business", fontWeight = FontWeight.Medium)
                            Text("Default to WhatsApp Business app", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(checked = preferWhatsAppBusiness, onCheckedChange = { scope.launch { preferences.setPreferWhatsAppBusiness(it) } })
                    }
                }
            }

            Text("Automated Reports", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryTeal)

            Card(shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Weekly Excel Sheet to Gmail", fontWeight = FontWeight.SemiBold)
                            Text("Receive automated weekly spreadsheet", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(checked = weeklyEmailEnabled, onCheckedChange = { scope.launch { preferences.setWeeklyEmailEnabled(it) } })
                    }

                    if (weeklyEmailEnabled) {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = {
                                emailInput = it
                                scope.launch { preferences.setWeeklyEmailAddress(it) }
                            },
                            label = { Text("Recipient Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Set 4-Digit PIN") },
            text = {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { if (it.length <= 4) pinInput = it },
                    label = { Text("Enter 4 digits") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput.length == 4) {
                            scope.launch {
                                preferences.setAppLockPinHash(CryptoUtils.hashPin(pinInput))
                                preferences.setAppLockEnabled(true)
                                showPinDialog = false
                            }
                        }
                    }
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language / भाषा चुनें") },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(LocaleHelper.supportedLanguages) { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        preferences.setSelectedLanguage(lang.code)
                                        LocaleHelper.setLocale(context, lang.code)
                                        showLanguageDialog = false
                                    }
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage == lang.code,
                                onClick = {
                                    scope.launch {
                                        preferences.setSelectedLanguage(lang.code)
                                        LocaleHelper.setLocale(context, lang.code)
                                        showLanguageDialog = false
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(lang.nativeName, fontWeight = FontWeight.SemiBold)
                                if (lang.nativeName != lang.englishName) {
                                    Text(lang.englishName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
