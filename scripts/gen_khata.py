from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/feature/khata/KhataViewModel.kt', '''
package com.mdesigns.khata.feature.khata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.core.utils.KhataBalanceCalculator
import com.mdesigns.khata.data.repository.KhataRepository
import com.mdesigns.khata.domain.model.PersonFilter
import com.mdesigns.khata.domain.model.PersonSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KhataViewModel @Inject constructor(
    private val repository: KhataRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(PersonFilter.ALL)
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _allSummaries = MutableStateFlow<List<PersonSummary>>(emptyList())

    val filteredSummaries: StateFlow<List<PersonSummary>> = combine(
        _allSummaries,
        _searchQuery,
        _selectedFilter
    ) { summaries, query, filter ->
        summaries.filter { item ->
            val matchesQuery = query.isEmpty() ||
                    item.person.name.contains(query, ignoreCase = true) ||
                    item.person.mobile.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                PersonFilter.ALL -> true
                PersonFilter.RECEIVABLE -> item.status == KhataBalanceCalculator.BalanceStatus.YOU_COLLECT
                PersonFilter.PAYABLE -> item.status == KhataBalanceCalculator.BalanceStatus.YOU_GIVE
                PersonFilter.SETTLED -> item.status == KhataBalanceCalculator.BalanceStatus.SETTLED
                PersonFilter.CUSTOMERS -> item.person.category.equals("Customer", ignoreCase = true)
                PersonFilter.SUPPLIERS -> item.person.category.equals("Supplier", ignoreCase = true)
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                repository.getPeopleSummaries(profileId).collectLatest { summaries ->
                    _allSummaries.value = summaries
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: PersonFilter) {
        _selectedFilter.value = filter
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/khata/KhataScreen.kt', '''
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
''')

w('app/src/main/java/com/mdesigns/khata/feature/person/AddEditPersonViewModel.kt', '''
package com.mdesigns.khata.feature.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.data.entity.LedgerPersonEntity
import com.mdesigns.khata.data.repository.KhataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditPersonViewModel @Inject constructor(
    private val repository: KhataRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    fun savePerson(
        personId: Long?,
        name: String,
        mobile: String,
        category: String,
        openingBalance: Double,
        isOpeningDebit: Boolean,
        address: String,
        notes: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val profileId = preferences.activeProfileId.first()
            val entity = LedgerPersonEntity(
                id = personId ?: 0L,
                profileId = profileId,
                name = name.trim(),
                mobile = mobile.trim(),
                category = category,
                openingBalance = openingBalance,
                isOpeningBalanceDebit = isOpeningDebit,
                address = address.trim(),
                notes = notes.trim(),
                updatedAt = System.currentTimeMillis()
            )

            val id = if (personId == null || personId == 0L) {
                repository.insertPerson(entity)
            } else {
                repository.updatePerson(entity)
                personId
            }
            onSuccess(id)
        }
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/person/AddEditPersonScreen.kt', '''
package com.mdesigns.khata.feature.person

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mdesigns.khata.core.theme.CreditGreen
import com.mdesigns.khata.core.theme.DebitRed
import com.mdesigns.khata.core.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPersonScreen(
    personId: Long? = null,
    onNavigateBack: () -> Unit,
    onPersonSaved: (Long) -> Unit,
    viewModel: AddEditPersonViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Customer") }
    var openingBalanceStr by remember { mutableStateOf("") }
    var isOpeningDebit by remember { mutableStateOf(true) }
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (personId == null) "Add Party / Person" else "Edit Person") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            nameError = "Name is required"
                            return@Button
                        }
                        val opBalance = openingBalanceStr.toDoubleOrNull() ?: 0.0
                        viewModel.savePerson(
                            personId = personId,
                            name = name,
                            mobile = mobile,
                            category = category,
                            openingBalance = opBalance,
                            isOpeningDebit = isOpeningDebit,
                            address = address,
                            notes = notes,
                            onSuccess = onPersonSaved
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text("Save Party", fontWeight = FontWeight.Bold)
                }
            }
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
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = null
                },
                label = { Text("Full Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text("Mobile Number (for WhatsApp/SMS)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Text(text = "Party Category", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Customer", "Supplier", "Personal").forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Opening Balance (Optional)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = openingBalanceStr,
                        onValueChange = { openingBalanceStr = it },
                        label = { Text("Amount (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Text(" ₹ ", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = isOpeningDebit,
                            onClick = { isOpeningDebit = true },
                            label = { Text("You Collect (Receivable)") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CreditGreen.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = !isOpeningDebit,
                            onClick = { isOpeningDebit = false },
                            label = { Text("You Give (Payable)") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DebitRed.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address / City (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Additional Remarks / Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                shape = MaterialTheme.shapes.medium,
                maxLines = 3
            )
        }
    }
}
''')
print("Khata screens generated")
