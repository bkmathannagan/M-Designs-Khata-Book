package com.mdesigns.khata.feature.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mdesigns.khata.core.excel.ExcelReportGenerator
import com.mdesigns.khata.core.pdf.PdfReportGenerator
import com.mdesigns.khata.core.sharing.ShareManager
import com.mdesigns.khata.core.theme.PrimaryTeal
import com.mdesigns.khata.feature.dashboard.DashboardViewModel
import com.mdesigns.khata.feature.khata.KhataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onNavigateBack: (() -> Unit)? = null,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isExporting by viewModel.isExporting.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val summaries by viewModel.peopleSummaries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports & Statements") },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Export & Share Business Documents",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Professional PDF & Excel statements generated with M Designs letterhead.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (statusMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PrimaryTeal.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = statusMessage!!,
                                color = PrimaryTeal,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.clearStatusMessage() }) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = PrimaryTeal)
                            }
                        }
                    }
                }
            }

            // PDF Full Ledger Report Card
            item {
                ReportActionCard(
                    title = "Full Khata Statement (PDF)",
                    description = "Comprehensive statement with all ${transactions.size} transactions, party details and running balance.",
                    icon = Icons.Default.PictureAsPdf,
                    buttonLabel = "Export PDF",
                    isLoading = isExporting
                ) {
                    viewModel.exportFullPdfReport(context)
                }
            }

            // Excel / CSV Export Card
            item {
                ReportActionCard(
                    title = "All Transactions (.CSV / Excel)",
                    description = "Export all ${transactions.size} records and party ledger balances to spreadsheet.",
                    icon = Icons.Default.TableChart,
                    buttonLabel = "Export Excel / CSV",
                    isLoading = isExporting
                ) {
                    viewModel.exportExcelCsvReport(context)
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Automated Weekly Report",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Receive scheduled Excel sheets directly to your email without manual exports. Configure under More > Settings.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    buttonLabel: String,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryTeal.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = PrimaryTeal)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onClick,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(buttonLabel, fontWeight = FontWeight.Bold)
            }
        }
    }
}
