from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/feature/more/profile/BusinessProfileScreen.kt', '''
package com.mdesigns.khata.feature.more.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mdesigns.khata.core.theme.PrimaryTeal
import com.mdesigns.khata.feature.dashboard.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val profile by viewModel.activeProfile.collectAsState()

    var name by remember(profile) { mutableStateOf(profile?.name ?: "") }
    var ownerName by remember(profile) { mutableStateOf(profile?.ownerName ?: "") }
    var mobile by remember(profile) { mutableStateOf(profile?.mobile ?: "") }
    var email by remember(profile) { mutableStateOf(profile?.email ?: "") }
    var gstin by remember(profile) { mutableStateOf(profile?.gstin ?: "") }
    var address by remember(profile) { mutableStateOf(profile?.address ?: "") }
    var savedMessage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(tonalElevation = 4.dp) {
                Button(
                    onClick = { savedMessage = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Save Profile", fontWeight = FontWeight.Bold)
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (savedMessage) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PrimaryTeal.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Profile updated successfully!", modifier = Modifier.padding(12.dp), color = PrimaryTeal, fontWeight = FontWeight.SemiBold)
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Business / Shop Name *") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = ownerName,
                onValueChange = { ownerName = it },
                label = { Text("Owner / Proprietor Name") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text("Business Phone") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = gstin,
                onValueChange = { gstin = it },
                label = { Text("GSTIN (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Business Address") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                shape = MaterialTheme.shapes.medium,
                maxLines = 3
            )
        }
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/more/businesscard/BusinessCardScreen.kt', '''
package com.mdesigns.khata.feature.more.businesscard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mdesigns.khata.core.sharing.ShareManager
import com.mdesigns.khata.core.theme.PrimaryTeal
import com.mdesigns.khata.feature.dashboard.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessCardScreen(
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val profile by viewModel.activeProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Card Maker") },
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryTeal),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = profile?.name ?: "M Designs Enterprise",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = profile?.ownerName ?: "Authorized Proprietor",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (profile?.mobile?.isNotEmpty() == true) {
                            Text(text = "📞 ${profile!!.mobile}", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                        }
                        if (profile?.email?.isNotEmpty() == true) {
                            Text(text = "✉️ ${profile!!.email}", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                        }
                        Text(text = "🌐 https://mdesignsrmd.com", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    val cardText = "Business Visiting Card\\n${profile?.name ?: "M Designs Business"}\\nContact: ${profile?.mobile}\\nWebsite: https://mdesignsrmd.com"
                    ShareManager.shareToWhatsApp(context, profile?.mobile, cardText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Visiting Card on WhatsApp")
            }
        }
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/feature/more/recyclebin/RecycleBinViewModel.kt', '''
package com.mdesigns.khata.feature.more.recyclebin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.data.entity.LedgerPersonEntity
import com.mdesigns.khata.data.entity.TransactionEntity
import com.mdesigns.khata.data.repository.KhataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecycleBinViewModel @Inject constructor(
    private val repository: KhataRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    private val _deletedPeople = MutableStateFlow<List<LedgerPersonEntity>>(emptyList())
    val deletedPeople: StateFlow<List<LedgerPersonEntity>> = _deletedPeople.asStateFlow()

    private val _deletedTransactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val deletedTransactions: StateFlow<List<TransactionEntity>> = _deletedTransactions.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                repository.getDeletedPeople(profileId).collectLatest { _deletedPeople.value = it }
            }
        }
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                repository.getDeletedTransactions(profileId).collectLatest { _deletedTransactions.value = it }
            }
        }
    }

    fun restorePerson(id: Long) = viewModelScope.launch { repository.restorePerson(id) }
    fun permanentlyDeletePerson(id: Long) = viewModelScope.launch { repository.permanentlyDeletePerson(id) }

    fun restoreTransaction(id: Long) = viewModelScope.launch { repository.restoreTransaction(id) }
    fun permanentlyDeleteTransaction(id: Long) = viewModelScope.launch { repository.permanentlyDeleteTransaction(id) }
}
''')
print("More P1 screens generated")
