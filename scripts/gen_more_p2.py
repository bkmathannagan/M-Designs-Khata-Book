from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/feature/more/recyclebin/RecycleBinScreen.kt', '''
package com.mdesigns.khata.feature.more.recyclebin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mdesigns.khata.core.theme.DebitRed
import com.mdesigns.khata.core.theme.PrimaryTeal
import com.mdesigns.khata.core.utils.CurrencyFormatter
import com.mdesigns.khata.core.utils.DateUtils
import com.mdesigns.khata.feature.components.EmptyStateMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecycleBinViewModel = hiltViewModel()
) {
    val deletedPeople by viewModel.deletedPeople.collectAsState()
    val deletedTxs by viewModel.deletedTransactions.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recycle Bin") },
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
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Parties (${deletedPeople.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Transactions (${deletedTxs.size})") }
                )
            }

            if (selectedTab == 0) {
                if (deletedPeople.isEmpty()) {
                    EmptyStateMessage(
                        title = "Recycle Bin is Empty",
                        subtitle = "No deleted parties found in the recycle bin.",
                        icon = Icons.Default.DeleteOutline
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(deletedPeople, key = { it.id }) { person ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = person.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                        Text(text = "Deleted: ${person.deletedAt?.let { DateUtils.formatDate(it) } ?: "Recently"}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { viewModel.restorePerson(person.id) }) {
                                        Icon(Icons.Default.Restore, contentDescription = "Restore", tint = PrimaryTeal)
                                    }
                                    IconButton(onClick = { viewModel.permanentlyDeletePerson(person.id) }) {
                                        Icon(Icons.Default.DeleteForever, contentDescription = "Delete Forever", tint = DebitRed)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (deletedTxs.isEmpty()) {
                    EmptyStateMessage(
                        title = "No Deleted Transactions",
                        subtitle = "No deleted transactions found in the recycle bin.",
                        icon = Icons.Default.DeleteOutline
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(deletedTxs, key = { it.id }) { tx ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "${tx.type}: ${CurrencyFormatter.format(tx.amount)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                        Text(text = "${tx.description} • ${DateUtils.formatDate(tx.date)}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { viewModel.restoreTransaction(tx.id) }) {
                                        Icon(Icons.Default.Restore, contentDescription = "Restore", tint = PrimaryTeal)
                                    }
                                    IconButton(onClick = { viewModel.permanentlyDeleteTransaction(tx.id) }) {
                                        Icon(Icons.Default.DeleteForever, contentDescription = "Delete Forever", tint = DebitRed)
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

w('app/src/main/java/com/mdesigns/khata/feature/more/settings/SettingsScreen.kt', '''
package com.mdesigns.khata.feature.more.settings

import androidx.compose.foundation.layout.*
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
    val weeklyEmailEnabled by preferences.weeklyEmailEnabled.collectAsState(initial = false)
    val weeklyEmailAddress by preferences.weeklyEmailAddress.collectAsState(initial = "")

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var emailInput by remember(weeklyEmailAddress) { mutableStateOf(weeklyEmailAddress) }

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
}
''')
print("More P2 screens generated")
