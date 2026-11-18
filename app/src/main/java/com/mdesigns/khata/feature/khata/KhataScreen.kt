package com.mdesigns.khata.feature.khata

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mdesigns.khata.core.sharing.ShareManager
import com.mdesigns.khata.core.theme.PrimaryTeal
import com.mdesigns.khata.domain.model.PersonFilter
import com.mdesigns.khata.feature.components.EmptyStateMessage
import com.mdesigns.khata.feature.components.PersonSummaryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhataScreen(
    onNavigateToAddPerson: () -> Unit,
    onNavigateToPersonLedger: (Long) -> Unit,
    viewModel: KhataViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val summaries by viewModel.filteredSummaries.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Khata Ledger") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddPerson() },
                containerColor = PrimaryTeal,
                contentColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Person")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search customer or party...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PersonFilter.values().forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { viewModel.onFilterSelected(filter) },
                        label = {
                            Text(
                                when (filter) {
                                    PersonFilter.ALL -> "All"
                                    PersonFilter.RECEIVABLE -> "You Collect"
                                    PersonFilter.PAYABLE -> "You Give"
                                    PersonFilter.SETTLED -> "Settled"
                                    PersonFilter.CUSTOMERS -> "Customers"
                                    PersonFilter.SUPPLIERS -> "Suppliers"
                                }
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (summaries.isEmpty()) {
                EmptyStateMessage(
                    title = "No Parties Found",
                    subtitle = if (searchQuery.isNotEmpty()) "No results matching '$searchQuery'" else "Add your first customer or supplier to start maintaining digital khata.",
                    icon = Icons.Default.PeopleOutline
                ) {
                    Button(
                        onClick = { onNavigateToAddPerson() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                    ) {
                        Text("Add Person")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(summaries, key = { it.person.id }) { summary ->
                        PersonSummaryItem(
                            summary = summary,
                            onClick = { onNavigateToPersonLedger(summary.person.id) },
                            onCallClick = {
                                if (summary.person.mobile.isNotEmpty()) {
                                    ShareManager.sendSms(context, summary.person.mobile, "")
                                }
                            },
                            onWhatsAppClick = {
                                val msg = "Namaste ${summary.person.name}, your balance with us is ₹${summary.netBalance}. Thank you."
                                ShareManager.shareToWhatsApp(context, summary.person.mobile, msg)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
