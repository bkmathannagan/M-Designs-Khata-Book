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
import com.mdesigns.khata.core.theme.CreditGreen
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
    val deletedMoneyTxs by viewModel.deletedMoneyTransactions.collectAsState()

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
                    text = { Text("Ledger (${deletedTxs.size})") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Cash Flow (${deletedMoneyTxs.size})") }
                )
            }

            when (selectedTab) {
                0 -> {
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
                }
                1 -> {
                    if (deletedTxs.isEmpty()) {
                        EmptyStateMessage(
                            title = "No Deleted Transactions",
                            subtitle = "No deleted ledger transactions found in the recycle bin.",
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
                2 -> {
                    if (deletedMoneyTxs.isEmpty()) {
                        EmptyStateMessage(
                            title = "No Deleted Cash Flow Records",
                            subtitle = "No deleted income or expense entries found in the recycle bin.",
                            icon = Icons.Default.DeleteOutline
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(deletedMoneyTxs, key = { it.id }) { mTx ->
                                val isIncome = mTx.type == "INCOME"
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
                                            Text(
                                                text = (if (isIncome) "+ Income: " else "- Expense: ") + CurrencyFormatter.format(mTx.amount),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isIncome) CreditGreen else DebitRed
                                            )
                                            Text(
                                                text = "${mTx.category} • ${if (mTx.description.isNotBlank()) "${mTx.description} • " else ""}${DateUtils.formatDate(mTx.date)}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        IconButton(onClick = { viewModel.restoreMoneyTransaction(mTx.id) }) {
                                            Icon(Icons.Default.Restore, contentDescription = "Restore", tint = PrimaryTeal)
                                        }
                                        IconButton(onClick = { viewModel.permanentlyDeleteMoneyTransaction(mTx.id) }) {
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
}

