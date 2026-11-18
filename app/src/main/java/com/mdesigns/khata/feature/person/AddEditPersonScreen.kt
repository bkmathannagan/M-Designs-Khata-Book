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

import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPersonScreen(
    personId: Long? = null,
    onNavigateBack: () -> Unit,
    onPersonSaved: (Long) -> Unit,
    viewModel: AddEditPersonViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Customer") }
    var openingBalanceStr by remember { mutableStateOf("") }
    var isOpeningDebit by remember { mutableStateOf(true) }
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { contactUri: Uri? ->
        contactUri?.let { uri ->
            try {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                        val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        val hasPhoneIndex = it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)

                        if (nameIndex != -1) {
                            val pickedName = it.getString(nameIndex)
                            if (!pickedName.isNullOrBlank()) {
                                name = pickedName
                                nameError = null
                            }
                        }

                        val contactId = if (idIndex != -1) it.getString(idIndex) else null
                        val hasPhone = if (hasPhoneIndex != -1) it.getInt(hasPhoneIndex) > 0 else true

                        if (contactId != null && hasPhone) {
                            val phoneCursor = context.contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                                arrayOf(contactId),
                                null
                            )
                            phoneCursor?.use { pc ->
                                if (pc.moveToFirst()) {
                                    val numberIndex = pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    if (numberIndex != -1) {
                                        val rawNumber = pc.getString(numberIndex) ?: ""
                                        mobile = rawNumber.replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (personId == null) "Add Party / Person" else "Edit Person") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { contactPickerLauncher.launch(null) }) {
                        Icon(Icons.Default.ContactPhone, contentDescription = "Import from Contacts", tint = PrimaryTeal)
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedButton(
                onClick = { contactPickerLauncher.launch(null) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ContactPhone, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select from Phone Contacts", color = PrimaryTeal, fontWeight = FontWeight.SemiBold)
            }
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
