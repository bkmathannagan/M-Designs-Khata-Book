package com.mdesigns.khata.feature.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mdesigns.khata.core.theme.PrimaryTeal
import com.mdesigns.khata.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("More Options & Tools") },
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
            item {
                Text(text = "Business Tools", style = MaterialTheme.typography.labelLarge, color = PrimaryTeal, fontWeight = FontWeight.Bold)
            }

            item {
                MoreMenuItem(
                    icon = Icons.Default.Business,
                    title = "Business Profile",
                    subtitle = "Shop name, logo, QR code, GSTIN, contacts",
                    onClick = { onNavigate(Screen.BusinessProfile.route) }
                )
            }

            item {
                MoreMenuItem(
                    icon = Icons.Default.Badge,
                    title = "Business Card Maker",
                    subtitle = "Create & share professional digital visiting cards",
                    onClick = { onNavigate(Screen.BusinessCard.route) }
                )
            }

            item {
                MoreMenuItem(
                    icon = Icons.Default.Description,
                    title = "Letterhead Designer",
                    subtitle = "Customize headers and stamps on PDF bills",
                    onClick = { onNavigate(Screen.Letterhead.route) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Data & Security", style = MaterialTheme.typography.labelLarge, color = PrimaryTeal, fontWeight = FontWeight.Bold)
            }

            item {
                MoreMenuItem(
                    icon = Icons.Default.DeleteOutline,
                    title = "Recycle Bin",
                    subtitle = "Restore deleted parties and transactions",
                    onClick = { onNavigate(Screen.RecycleBin.route) }
                )
            }

            item {
                MoreMenuItem(
                    icon = Icons.Default.CloudSync,
                    title = "Backup & Restore",
                    subtitle = "Google Drive cloud backup and local export",
                    onClick = { onNavigate(Screen.BackupRestore.route) }
                )
            }

            item {
                MoreMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Settings & App Lock",
                    subtitle = "PIN security, dark mode, WhatsApp templates",
                    onClick = { onNavigate(Screen.Settings.route) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "General & Help", style = MaterialTheme.typography.labelLarge, color = PrimaryTeal, fontWeight = FontWeight.Bold)
            }

            item {
                MoreMenuItem(
                    icon = Icons.Default.Calculate,
                    title = "Cash Denomination Counter",
                    subtitle = "Quick ₹500, ₹200, ₹100, ₹50 note counter",
                    onClick = { onNavigate(Screen.Denomination.route) }
                )
            }

            item {
                MoreMenuItem(
                    icon = Icons.Default.CalendarToday,
                    title = "Bank Holidays",
                    subtitle = "Indian bank holidays & settlement calendar",
                    onClick = { onNavigate(Screen.BankHolidays.route) }
                )
            }

            item {
                MoreMenuItem(
                    icon = Icons.Default.Info,
                    title = "About M Designs Khata",
                    subtitle = "Version, copyright © M Designs, website",
                    onClick = { onNavigate(Screen.About.route) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
private fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = PrimaryTeal.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = PrimaryTeal)
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}
