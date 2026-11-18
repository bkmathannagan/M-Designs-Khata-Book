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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mdesigns.khata.core.sharing.ShareManager
import com.mdesigns.khata.core.theme.*
import com.mdesigns.khata.core.utils.CurrencyFormatter
import com.mdesigns.khata.core.utils.DateUtils
import com.mdesigns.khata.data.entity.MoneyTransactionEntity
import com.mdesigns.khata.feature.components.EmptyStateMessage
import com.mdesigns.khata.feature.components.PinVerificationDialog

private enum class MoneyAction { EDIT, DELETE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyScreen(
    viewModel: MoneyViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val transactions by viewModel.transactions.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsState()
    val appLockPinHash by viewModel.appLockPinHash.collectAsState()

    var showAddDialogForType by remember { mutableStateOf<String?>(null) } // "INCOME" or "EXPENSE"
    var selectedTxForActions by remember { mutableStateOf<MoneyTransactionEntity?>(null) }
    var editingTransaction by remember { mutableStateOf<MoneyTransactionEntity?>(null) }
    var deletingTransaction by remember { mutableStateOf<MoneyTransactionEntity?>(null) }
    var pendingPinAction by remember { mutableStateOf<Pair<MoneyAction, MoneyTransactionEntity>?>(null) }

    fun requestSecureAction(action: MoneyAction, tx: MoneyTransactionEntity) {
        val requiresPin = isAppLockEnabled && !appLockPinHash.isNullOrBlank()
        if (requiresPin) {
            pendingPinAction = Pair(action, tx)
        } else {
            when (action) {
                MoneyAction.EDIT -> editingTransaction = tx
                MoneyAction.DELETE -> deletingTransaction = tx
            }
        }
    }

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
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTxForActions = tx },
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

    // Add Transaction Dialog
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

    // Action Modal for Selected Transaction
    if (selectedTxForActions != null) {
        val tx = selectedTxForActions!!
        val isIncome = tx.type == "INCOME"

        AlertDialog(
            onDismissRequest = { selectedTxForActions = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = (if (isIncome) CreditGreen else DebitRed).copy(alpha = 0.15f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (isIncome) CreditGreen else DebitRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = (if (isIncome) "+" else "-") + CurrencyFormatter.format(tx.amount),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isIncome) CreditGreen else DebitRed
                        )
                        Text(
                            text = "${tx.category} • ${DateUtils.formatDateTime(tx.date)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralGray
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (tx.description.isNotBlank()) {
                        Text(
                            text = "Remarks: ${tx.description}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "Payment Mode: ${tx.paymentMode}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(4.dp))

                    // Edit Action
                    OutlinedButton(
                        onClick = {
                            val target = tx
                            selectedTxForActions = null
                            requestSecureAction(MoneyAction.EDIT, target)
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Transaction", color = PrimaryTeal, fontWeight = FontWeight.SemiBold)
                    }

                    // Duplicate Action
                    OutlinedButton(
                        onClick = {
                            viewModel.duplicateTransaction(tx)
                            selectedTxForActions = null
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Duplicate Entry", fontWeight = FontWeight.SemiBold)
                    }

                    // Share Action
                    OutlinedButton(
                        onClick = {
                            val shareContent = "${if (isIncome) "Income" else "Expense"} Receipt\n" +
                                    "Amount: ₹${CurrencyFormatter.format(tx.amount)}\n" +
                                    "Category: ${tx.category}\n" +
                                    "Mode: ${tx.paymentMode}\n" +
                                    "Date: ${DateUtils.formatDateTime(tx.date)}\n" +
                                    (if (tx.description.isNotBlank()) "Note: ${tx.description}\n" else "") +
                                    "— M Designs Khata Book"
                            ShareManager.shareText(context, shareContent, "Share Receipt")
                            selectedTxForActions = null
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share Receipt", fontWeight = FontWeight.SemiBold)
                    }

                    // Delete Action
                    TextButton(
                        onClick = {
                            val target = tx
                            selectedTxForActions = null
                            requestSecureAction(MoneyAction.DELETE, target)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = DebitRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Move to Recycle Bin", color = DebitRed, fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedTxForActions = null }) {
                    Text("Close")
                }
            }
        )
    }

    // PIN Verification Dialog
    if (pendingPinAction != null) {
        val (action, tx) = pendingPinAction!!
        PinVerificationDialog(
            storedPinHash = appLockPinHash,
            title = if (action == MoneyAction.EDIT) "PIN Required to Edit" else "PIN Required to Delete",
            subtitle = if (action == MoneyAction.EDIT) "Enter your 4-digit PIN to edit this entry" else "Enter your 4-digit PIN to delete this entry",
            onVerified = {
                pendingPinAction = null
                when (action) {
                    MoneyAction.EDIT -> editingTransaction = tx
                    MoneyAction.DELETE -> deletingTransaction = tx
                }
            },
            onDismiss = { pendingPinAction = null }
        )
    }

    // Edit Transaction Dialog
    if (editingTransaction != null) {
        val tx = editingTransaction!!
        var editType by remember(tx) { mutableStateOf(tx.type) }
        var editAmountStr by remember(tx) { mutableStateOf(tx.amount.toString()) }
        var editDescription by remember(tx) { mutableStateOf(tx.description) }
        var editCategory by remember(tx) { mutableStateOf(tx.category) }
        var editPaymentMode by remember(tx) { mutableStateOf(tx.paymentMode) }

        AlertDialog(
            onDismissRequest = { editingTransaction = null },
            title = {
                Text(
                    text = "Edit ${if (editType == "INCOME") "Income" else "Expense"}",
                    fontWeight = FontWeight.Bold,
                    color = if (editType == "INCOME") CreditGreen else DebitRed
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Type Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = editType == "INCOME",
                            onClick = {
                                editType = "INCOME"
                                if (editCategory in listOf("Purchase", "Rent", "Salary", "Bills")) editCategory = "Sales"
                            },
                            label = { Text("+ Income") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CreditGreen.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = editType == "EXPENSE",
                            onClick = {
                                editType = "EXPENSE"
                                if (editCategory in listOf("Sales", "Services")) editCategory = "Purchase"
                            },
                            label = { Text("- Expense") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DebitRed.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = editAmountStr,
                        onValueChange = { editAmountStr = it },
                        label = { Text("Amount *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Text(" ₹ ", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )

                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Description / Remarks") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )

                    Text(text = "Category", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val sampleCats = if (editType == "INCOME") listOf("Sales", "Services", "Other") else listOf("Purchase", "Rent", "Salary", "Bills")
                        sampleCats.forEach { cat ->
                            FilterChip(
                                selected = editCategory == cat,
                                onClick = { editCategory = cat },
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
                                selected = editPaymentMode == mode,
                                onClick = { editPaymentMode = mode },
                                label = { Text(mode) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = editAmountStr.toDoubleOrNull()
                        if (amt != null && amt > 0) {
                            viewModel.updateTransaction(
                                tx.copy(
                                    type = editType,
                                    amount = amt,
                                    category = editCategory,
                                    description = editDescription.trim(),
                                    paymentMode = editPaymentMode
                                )
                            )
                            editingTransaction = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (editType == "INCOME") CreditGreen else DebitRed)
                ) {
                    Text("Update Record")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTransaction = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (deletingTransaction != null) {
        val tx = deletingTransaction!!
        AlertDialog(
            onDismissRequest = { deletingTransaction = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = DebitRed) },
            title = { Text("Delete Transaction") },
            text = {
                Text("Are you sure you want to move this ${if (tx.type == "INCOME") "Income" else "Expense"} of ₹${CurrencyFormatter.format(tx.amount)} (${tx.category}) to the Recycle Bin? You can restore it later if needed.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTransaction(tx.id)
                        deletingTransaction = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DebitRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingTransaction = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

