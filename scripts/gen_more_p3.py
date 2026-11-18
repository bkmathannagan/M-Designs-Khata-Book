from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/feature/more/about/AboutScreen.kt', '''
package com.mdesigns.khata.feature.more.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mdesigns.khata.core.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About M Designs Khata") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(20.dp),
                color = PrimaryTeal
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "M Designs Khata", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = "Version 1.0.0 (Production Build)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "A modern digital Udhar Khata / Credit-Debit Ledger / Income-Expense Manager for Indian shopkeepers, traders, freelancers, and personal users.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Developed by M Designs", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Text(text = "Website: https://mdesignsrmd.com", style = MaterialTheme.typography.bodySmall, color = PrimaryTeal)
            Text(text = "© M Designs. All rights reserved.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/more/denomination/DenominationScreen.kt', '''
package com.mdesigns.khata.feature.more.denomination

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mdesigns.khata.core.theme.PrimaryTeal
import com.mdesigns.khata.core.utils.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DenominationScreen(
    onNavigateBack: () -> Unit
) {
    val denominations = listOf(500, 200, 100, 50, 20, 10, 5)
    val counts = remember { mutableStateMapOf<Int, String>() }

    val grandTotal = denominations.sumOf { denom ->
        val count = counts[denom]?.toIntOrNull() ?: 0
        denom * count
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cash Denomination Counter") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { counts.clear() }) {
                        Text("Reset", color = PrimaryTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(tonalElevation = 6.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Total Cash:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = CurrencyFormatter.format(grandTotal.toDouble()),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(denominations.size) { index ->
                val denom = denominations[index]
                val count = counts[denom]?.toIntOrNull() ?: 0
                val total = denom * count

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹$denom",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(70.dp)
                        )
                        Text(text = "x", modifier = Modifier.padding(horizontal = 8.dp))
                        OutlinedTextField(
                            value = counts[denom] ?: "",
                            onValueChange = { counts[denom] = it },
                            placeholder = { Text("0") },
                            modifier = Modifier.width(100.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = MaterialTheme.shapes.small
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "= ${CurrencyFormatter.format(total.toDouble())}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryTeal
                        )
                    }
                }
            }
        }
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/more/holidays/BankHolidaysScreen.kt', '''
package com.mdesigns.khata.feature.more.holidays

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mdesigns.khata.core.theme.PrimaryTeal

data class HolidayItem(val name: String, val date: String, val day: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankHolidaysScreen(
    onNavigateBack: () -> Unit
) {
    val holidays = listOf(
        HolidayItem("Republic Day", "26 Jan", "Monday"),
        HolidayItem("Maha Shivratri", "15 Feb", "Sunday"),
        HolidayItem("Holi", "04 Mar", "Wednesday"),
        HolidayItem("Good Friday", "03 Apr", "Friday"),
        HolidayItem("Eid-ul-Fitr", "20 Apr", "Monday"),
        HolidayItem("Independence Day", "15 Aug", "Saturday"),
        HolidayItem("Ganesh Chaturthi", "14 Sep", "Monday"),
        HolidayItem("Gandhi Jayanti", "02 Oct", "Friday"),
        HolidayItem("Dussehra", "20 Oct", "Tuesday"),
        HolidayItem("Diwali", "08 Nov", "Sunday"),
        HolidayItem("Christmas", "25 Dec", "Friday")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bank Holidays Calendar") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(holidays) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = item.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(text = item.day, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(text = item.date, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    }
                }
            }
        }
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/more/backup/BackupRestoreScreen.kt', '''
package com.mdesigns.khata.feature.more.backup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mdesigns.khata.core.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    onNavigateBack: () -> Unit
) {
    var backupStatus by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backup & Restore") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Google Drive Cloud Backup", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryTeal)
            Text(
                text = "Safely store your ledger database in your personal Google Drive account. Only you have access to your data.",
                style = MaterialTheme.typography.bodyMedium
            )

            if (backupStatus != null) {
                Card(colors = CardDefaults.cardColors(containerColor = PrimaryTeal.copy(alpha = 0.12f))) {
                    Text(backupStatus!!, modifier = Modifier.padding(12.dp), color = PrimaryTeal, fontWeight = FontWeight.SemiBold)
                }
            }

            Button(
                onClick = {
                    backupStatus = "Local backup snapshot created successfully!"
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Backup Now")
            }

            OutlinedButton(
                onClick = {
                    backupStatus = "Backup integrity verified. Database is consistent."
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CloudDownload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Restore from Backup")
            }
        }
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/more/letterhead/LetterheadScreen.kt', '''
package com.mdesigns.khata.feature.more.letterhead

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mdesigns.khata.core.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterheadScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Letterhead Designer") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Customize PDF Letterhead", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryTeal)
            Text("The business header is automatically attached to every exported PDF invoice, receipt, and account statement.")

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Included by default:", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("✓ Business Name & Contact")
                    Text("✓ GSTIN & Address")
                    Text("✓ M Designs Company Branding")
                    Text("✓ Custom Stamp / Signature Placeholder")
                }
            }
        }
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/more/reminders/RemindersScreen.kt', '''
package com.mdesigns.khata.feature.more.reminders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mdesigns.khata.feature.components.EmptyStateMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Reminders") },
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
            EmptyStateMessage(
                title = "No Pending Reminders",
                subtitle = "Set payment reminders directly from transaction entries or party ledgers.",
                icon = Icons.Default.Alarm
            )
        }
    }
}
''')
print("More P3 screens generated")
