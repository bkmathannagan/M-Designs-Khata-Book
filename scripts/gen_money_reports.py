from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/feature/money/MoneyViewModel.kt', '''
package com.mdesigns.khata.feature.money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.data.entity.CategoryEntity
import com.mdesigns.khata.data.entity.MoneyTransactionEntity
import com.mdesigns.khata.data.repository.MoneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoneyViewModel @Inject constructor(
    private val repository: MoneyRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<MoneyTransactionEntity>>(emptyList())
    val transactions: StateFlow<List<MoneyTransactionEntity>> = _transactions.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

    val totalIncome = _transactions.map { list -> list.filter { it.type == "INCOME" }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense = _transactions.map { list -> list.filter { it.type == "EXPENSE" }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                repository.getAllMoneyTransactions(profileId).collectLatest { txs ->
                    _transactions.value = txs
                }
            }
        }
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                repository.getCategories(profileId).collectLatest { cats ->
                    _categories.value = cats
                }
            }
        }
    }

    fun addTransaction(type: String, amount: Double, category: String, description: String, paymentMode: String) {
        viewModelScope.launch {
            val profileId = preferences.activeProfileId.first()
            val entity = MoneyTransactionEntity(
                profileId = profileId,
                type = type,
                amount = amount,
                category = category,
                description = description.trim(),
                paymentMode = paymentMode
            )
            repository.insertMoneyTransaction(entity)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.softDeleteMoneyTransaction(id)
        }
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/money/MoneyScreen.kt', '''
package com.mdesigns.khata.feature.money

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.mdesigns.khata.core.theme.*
import com.mdesigns.khata.core.utils.CurrencyFormatter
import com.mdesigns.khata.core.utils.DateUtils
import com.mdesigns.khata.feature.components.EmptyStateMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyScreen(
    viewModel: MoneyViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()

    var showAddDialogForType by remember { mutableStateOf<String?>(null) } // "INCOME" or "EXPENSE"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Income & Expense Manager") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(tonalElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showAddDialogForType = "INCOME" },
                        colors = ButtonDefaults.buttonColors(containerColor = CreditGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Income", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showAddDialogForType = "EXPENSE" },
                        colors = ButtonDefaults.buttonColors(containerColor = DebitRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("- Expense", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Cash Flow Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Net Cash Flow", style = MaterialTheme.typography.labelMedium, color = NeutralGray)
                    Text(
                        text = CurrencyFormatter.format(totalIncome - totalExpense),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (totalIncome >= totalExpense) CreditGreen else DebitRed
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Income: ${CurrencyFormatter.format(totalIncome)}", color = CreditGreen, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(text = "Total Expense: ${CurrencyFormatter.format(totalExpense)}", color = DebitRed, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (transactions.isEmpty()) {
                EmptyStateMessage(
                    title = "No Income/Expense Records",
                    subtitle = "Tap '+ Income' or '- Expense' below to start tracking your cash flow.",
                    icon = Icons.Default.Paid
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(transactions, key = { it.id }) { tx ->
                        val isIncome = tx.type == "INCOME"
                        Card(
                            modifier = Modifier.fillMaxWidth(),
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
                                    color = (if (isIncome) CreditGreen else DebitRed).copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            tint = if (isIncome) CreditGreen else DebitRed
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (tx.description.isNotEmpty()) tx.description else tx.category,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${tx.category} • ${DateUtils.formatDateTime(tx.date)} • ${tx.paymentMode}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NeutralGray
                                    )
                                }
                                Text(
                                    text = (if (isIncome) "+" else "-") + CurrencyFormatter.format(tx.amount),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isIncome) CreditGreen else DebitRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialogForType != null) {
        val type = showAddDialogForType!!
        var amountStr by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var category by remember { mutableStateOf(if (type == "INCOME") "Sales" else "Purchase") }
        var paymentMode by remember { mutableStateOf("Cash") }

        AlertDialog(
            onDismissRequest = { showAddDialogForType = null },
            title = {
                Text(
                    text = if (type == "INCOME") "Record Income (₹)" else "Record Expense (₹)",
                    fontWeight = FontWeight.Bold,
                    color = if (type == "INCOME") CreditGreen else DebitRed
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Amount *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Text(" ₹ ", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description / Remarks") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )

                    Text(text = "Category", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val sampleCats = if (type == "INCOME") listOf("Sales", "Services", "Other") else listOf("Purchase", "Rent", "Salary", "Bills")
                        sampleCats.forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat) }
                            )
                        }
                    }

                    Text(text = "Payment Mode", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Cash", "UPI", "Bank").forEach { mode ->
                            FilterChip(
                                selected = paymentMode == mode,
                                onClick = { paymentMode = mode },
                                label = { Text(mode) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountStr.toDoubleOrNull()
                        if (amt != null && amt > 0) {
                            viewModel.addTransaction(type, amt, category, description, paymentMode)
                            showAddDialogForType = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (type == "INCOME") CreditGreen else DebitRed)
                ) {
                    Text("Save Record")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialogForType = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/reports/ReportsScreen.kt', '''
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
    khataViewModel: KhataViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val summaries by khataViewModel.filteredSummaries.collectAsState()
    val profile by dashboardViewModel.activeProfile.collectAsState()
    val recentTxs by dashboardViewModel.recentTransactions.collectAsState()

    var showSuccessMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports & Statements") },
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

            // PDF Full Ledger Report Card
            item {
                ReportActionCard(
                    title = "Full Khata Statement (PDF)",
                    description = "Comprehensive party balance statement with opening balance and totals.",
                    icon = Icons.Default.PictureAsPdf,
                    buttonLabel = "Export PDF"
                ) {
                    val reportItems = recentTxs.map {
                        PdfReportGenerator.ReportItem(
                            date = it.date,
                            description = it.description,
                            debit = if (it.type == "DEBIT") it.amount else 0.0,
                            credit = if (it.type == "CREDIT") it.amount else 0.0,
                            runningBalance = it.amount
                        )
                    }
                    val pdfData = PdfReportGenerator.ReportData(
                        businessName = profile?.name ?: "M Designs Khata",
                        businessPhone = profile?.mobile ?: "",
                        personName = "All Ledger Accounts",
                        personPhone = "",
                        openingBalance = 0.0,
                        isOpeningDebit = true,
                        items = reportItems,
                        totalDebit = recentTxs.filter { it.type == "DEBIT" }.sumOf { it.amount },
                        totalCredit = recentTxs.filter { it.type == "CREDIT" }.sumOf { it.amount },
                        closingBalance = 0.0
                    )
                    val file = PdfReportGenerator.generatePersonStatement(context, pdfData)
                    ShareManager.shareFile(context, file, "application/pdf", "Share Full Statement PDF")
                }
            }

            // Excel / CSV Export Card
            item {
                ReportActionCard(
                    title = "All Transactions (.CSV / Excel)",
                    description = "Export all customer ledgers, debit/credit records, and categories to spreadsheet.",
                    icon = Icons.Default.TableChart,
                    buttonLabel = "Export Excel / CSV"
                ) {
                    val rows = recentTxs.map {
                        ExcelReportGenerator.ExportRow(
                            date = it.date,
                            personName = "Party #${it.personId}",
                            description = it.description,
                            type = it.type,
                            amount = it.amount,
                            paymentMode = it.paymentMode,
                            balance = it.amount
                        )
                    }
                    val file = ExcelReportGenerator.exportToCsv(context, "M_Designs_Khata_Export", rows)
                    ShareManager.shareFile(context, file, "text/csv", "Share Excel / CSV Export")
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
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonLabel)
            }
        }
    }
}
''')
print("Money and Reports screens generated")
