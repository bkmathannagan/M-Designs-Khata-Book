from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/feature/dashboard/DashboardViewModel.kt', '''
package com.mdesigns.khata.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.core.utils.KhataBalanceCalculator
import com.mdesigns.khata.data.entity.BusinessProfileEntity
import com.mdesigns.khata.data.entity.TransactionEntity
import com.mdesigns.khata.data.repository.KhataRepository
import com.mdesigns.khata.data.repository.MoneyRepository
import com.mdesigns.khata.data.repository.ProfileRepository
import com.mdesigns.khata.domain.model.DashboardSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val khataRepository: KhataRepository,
    private val moneyRepository: MoneyRepository,
    private val profileRepository: ProfileRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    private val _activeProfile = MutableStateFlow<BusinessProfileEntity?>(null)
    val activeProfile: StateFlow<BusinessProfileEntity?> = _activeProfile.asStateFlow()

    private val _summary = MutableStateFlow(DashboardSummary())
    val summary: StateFlow<DashboardSummary> = _summary.asStateFlow()

    private val _recentTransactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val recentTransactions: StateFlow<List<TransactionEntity>> = _recentTransactions.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                loadProfileData(profileId)
            }
        }
    }

    private fun loadProfileData(profileId: Long) {
        viewModelScope.launch {
            _activeProfile.value = profileRepository.getProfileById(profileId) ?: profileRepository.getDefaultProfile()

            khataRepository.getPeopleSummaries(profileId).collectLatest { people ->
                val receivable = people.filter { it.status == KhataBalanceCalculator.BalanceStatus.YOU_COLLECT }.sumOf { it.netBalance }
                val payable = people.filter { it.status == KhataBalanceCalculator.BalanceStatus.YOU_GIVE }.sumOf { kotlin.math.abs(it.netBalance) }
                val net = receivable - payable
                val settled = people.count { it.status == KhataBalanceCalculator.BalanceStatus.SETTLED }
                val pending = people.size - settled
                val customers = people.count { it.person.category == "Customer" }
                val suppliers = people.count { it.person.category == "Supplier" }

                val todayIncome = moneyRepository.getTodayIncome(profileId)
                val todayExpense = moneyRepository.getTodayExpense(profileId)
                val monthIncome = moneyRepository.getMonthIncome(profileId)
                val monthExpense = moneyRepository.getMonthExpense(profileId)

                _summary.value = DashboardSummary(
                    totalReceivable = receivable,
                    totalPayable = payable,
                    netBalance = net,
                    todayIncome = todayIncome,
                    todayExpense = todayExpense,
                    monthIncome = monthIncome,
                    monthExpense = monthExpense,
                    customerCount = customers,
                    supplierCount = suppliers,
                    settledCount = settled,
                    pendingCount = pending
                )
            }
        }

        viewModelScope.launch {
            khataRepository.getRecentTransactions(profileId, 10).collectLatest { txs ->
                _recentTransactions.value = txs
            }
        }
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/dashboard/HomeScreen.kt', '''
package com.mdesigns.khata.feature.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mdesigns.khata.core.theme.*
import com.mdesigns.khata.core.utils.CurrencyFormatter
import com.mdesigns.khata.core.utils.DateUtils
import com.mdesigns.khata.feature.components.SummaryStatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToKhata: () -> Unit,
    onNavigateToMoney: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToAddPerson: () -> Unit,
    onNavigateToPersonLedger: (Long) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val summary by viewModel.summary.collectAsState()
    val profile by viewModel.activeProfile.collectAsState()
    val recentTxs by viewModel.recentTransactions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = profile?.name ?: "M Designs Khata",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Credit • Debit • Cash Book",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToReports() }) {
                        Icon(Icons.Default.Assessment, contentDescription = "Reports", tint = PrimaryTeal)
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryStatCard(
                        title = "You Collect",
                        amount = summary.totalReceivable,
                        caption = "${summary.pendingCount} Pending Accounts",
                        icon = Icons.Default.ArrowDownward,
                        color = CreditGreen,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToKhata
                    )
                    SummaryStatCard(
                        title = "You Give",
                        amount = summary.totalPayable,
                        caption = "${summary.customerCount} Customers",
                        icon = Icons.Default.ArrowUpward,
                        color = DebitRed,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToKhata
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Quick Actions",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            QuickActionButton(icon = Icons.Default.PersonAdd, label = "Add Party", color = PrimaryTeal) {
                                onNavigateToAddPerson()
                            }
                            QuickActionButton(icon = Icons.Default.TrendingUp, label = "Income", color = CreditGreen) {
                                onNavigateToMoney()
                            }
                            QuickActionButton(icon = Icons.Default.TrendingDown, label = "Expense", color = DebitRed) {
                                onNavigateToMoney()
                            }
                            QuickActionButton(icon = Icons.Default.PictureAsPdf, label = "Reports", color = PrimaryTeal) {
                                onNavigateToReports()
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today's Cash Flow",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "View Money >",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryTeal,
                                modifier = Modifier.clickable { onNavigateToMoney() }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Income", style = MaterialTheme.typography.labelSmall, color = NeutralGray)
                                Text(
                                    text = CurrencyFormatter.format(summary.todayIncome),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = CreditGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Expense", style = MaterialTheme.typography.labelSmall, color = NeutralGray)
                                Text(
                                    text = CurrencyFormatter.format(summary.todayExpense),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = DebitRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "View All >",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryTeal,
                        modifier = Modifier.clickable { onNavigateToKhata() }
                    )
                }
            }

            if (recentTxs.isEmpty()) {
                item {
                    Text(
                        text = "No recent transactions. Tap '+ Add Person' or record an entry to start.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(recentTxs) { tx ->
                    val isDebit = tx.type == "DEBIT"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToPersonLedger(tx.personId) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isDebit) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (isDebit) DebitRed else CreditGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (tx.description.isNotEmpty()) tx.description else (if (isDebit) "Debit / You Gave" else "Credit / You Received"),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${DateUtils.formatDateTime(tx.date)} • ${tx.paymentMode}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NeutralGray
                                )
                            }
                            Text(
                                text = (if (isDebit) "-" else "+") + CurrencyFormatter.format(tx.amount),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDebit) DebitRed else CreditGreen
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(14.dp),
            color = color.copy(alpha = 0.12f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
    }
}
''')
print("Dashboard generated")
