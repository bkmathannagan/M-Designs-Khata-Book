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
    var isSaving by remember { mutableStateOf(false) }
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
                    onClick = {
                        isSaving = true
                        viewModel.updateBusinessProfile(
                            name = name,
                            ownerName = ownerName,
                            mobile = mobile,
                            email = email,
                            gstin = gstin,
                            address = address
                        ) { success ->
                            isSaving = false
                            if (success) {
                                savedMessage = true
                            }
                        }
                    },
                    enabled = !isSaving && name.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (isSaving) "Saving..." else "Save Profile", fontWeight = FontWeight.Bold)
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
