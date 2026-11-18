package com.mdesigns.khata.feature.ledger

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mdesigns.khata.core.pdf.PdfReportGenerator
import com.mdesigns.khata.core.sharing.ShareManager
import com.mdesigns.khata.core.theme.*
import com.mdesigns.khata.core.utils.CurrencyFormatter
import com.mdesigns.khata.core.utils.DateUtils
import com.mdesigns.khata.core.utils.KhataBalanceCalculator
import com.mdesigns.khata.core.utils.UpiHelper
import com.mdesigns.khata.data.entity.TransactionEntity
import com.mdesigns.khata.feature.components.EmptyStateMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonLedgerScreen(
    onNavigateBack: () -> Unit,
    viewModel: LedgerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val person by viewModel.person.collectAsState()
    val businessProfile by viewModel.businessProfile.collectAsState()
    val entries by viewModel.entries.collectAsState()
    val balanceResult by viewModel.balanceResult.collectAsState()

    var showEntryDialogForType by remember { mutableStateOf<String?>(null) } // "DEBIT" or "CREDIT"
    var selectedTxForActions by remember { mutableStateOf<TransactionEntity?>(null) }
    var showUpiQrDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = person?.name ?: "Party Ledger", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (person?.mobile?.isNotEmpty() == true) {
                            Text(text = person!!.mobile, style = MaterialTheme.typography.labelSmall, color = NeutralGray)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showUpiQrDialog = true }) {
                        Icon(Icons.Default.QrCode2, contentDescription = "UPI QR", tint = PrimaryTeal)
                    }

                    IconButton(onClick = {
                        person?.let { p ->
                            val reportItems = entries.map {
                                PdfReportGenerator.ReportItem(
                                    date = it.transaction.date,
                                    description = it.transaction.description,
                                    debit = if (it.transaction.type == "DEBIT") it.transaction.amount else 0.0,
                                    credit = if (it.transaction.type == "CREDIT") it.transaction.amount else 0.0,
                                    runningBalance = it.runningBalance
                                )
                            }
                            val pdfData = PdfReportGenerator.ReportData(
                                businessName = businessProfile?.name ?: "M Designs Khata",
                                businessPhone = businessProfile?.mobile ?: "",
                                personName = p.name,
                                personPhone = p.mobile,
                                openingBalance = p.openingBalance,
                                isOpeningDebit = p.isOpeningBalanceDebit,
                                items = reportItems,
                                totalDebit = balanceResult?.totalDebit ?: 0.0,
                                totalCredit = balanceResult?.totalCredit ?: 0.0,
                                closingBalance = balanceResult?.netBalance ?: 0.0
                            )
                            val file = PdfReportGenerator.generatePersonStatement(context, pdfData)
                            ShareManager.shareFile(context, file, "application/pdf", "Share Statement PDF")
                        }
                    }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = PrimaryTeal)
                    }

                    IconButton(onClick = {
                        person?.let { p ->
                            val msg = "Dear ${p.name},\nYour balance with ${businessProfile?.name ?: "us"} is ₹${balanceResult?.displayAmount ?: 0.0} (${if ((balanceResult?.netBalance ?: 0.0) >= 0) "You Collect" else "You Give"}).\nThank you."
                            ShareManager.shareToWhatsApp(context, p.mobile, msg)
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = PrimaryTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(tonalElevation = 6.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showEntryDialogForType = "DEBIT" },
                        colors = ButtonDefaults.buttonColors(containerColor = DebitRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("You Give (₹)", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showEntryDialogForType = "CREDIT" },
                        colors = ButtonDefaults.buttonColors(containerColor = CreditGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("You Receive (₹)", fontWeight = FontWeight.Bold)
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
            // Balance Summary Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (balanceResult?.status) {
                        KhataBalanceCalculator.BalanceStatus.YOU_COLLECT -> CreditGreenContainer.copy(alpha = 0.4f)
                        KhataBalanceCalculator.BalanceStatus.YOU_GIVE -> DebitRedContainer.copy(alpha = 0.4f)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = when (balanceResult?.status) {
                                    KhataBalanceCalculator.BalanceStatus.YOU_COLLECT -> "You Collect (Receivable)"
                                    KhataBalanceCalculator.BalanceStatus.YOU_GIVE -> "You Give (Payable)"
                                    else -> "Settled (Zero Balance)"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = NeutralGray
                            )
                            Text(
                                text = CurrencyFormatter.format(balanceResult?.displayAmount ?: 0.0),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = when (balanceResult?.status) {
                                    KhataBalanceCalculator.BalanceStatus.YOU_COLLECT -> CreditGreen
                                    KhataBalanceCalculator.BalanceStatus.YOU_GIVE -> DebitRed
                                    else -> NeutralGray
                                }
                            )
                        }

                        // WhatsApp Action
                        IconButton(
                            onClick = {
                                person?.let { p ->
                                    val msg = "Dear ${p.name}, your balance with us is ₹${balanceResult?.displayAmount ?: 0.0}. Please verify."
                                    ShareManager.shareToWhatsApp(context, p.mobile, msg)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send Reminder", tint = PrimaryTeal)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Given: ${CurrencyFormatter.format(balanceResult?.totalDebit ?: 0.0)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = DebitRed
                        )
                        Text(
                            text = "Total Received: ${CurrencyFormatter.format(balanceResult?.totalCredit ?: 0.0)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CreditGreen
                        )
                    }
                }
            }

            // Entries List
            if (entries.isEmpty()) {
                EmptyStateMessage(
                    title = "No Entries Yet",
                    subtitle = "Tap 'You Give' or 'You Receive' below to add transactions.",
                    icon = Icons.Default.ReceiptLong
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(entries, key = { it.transaction.id }) { entry ->
                        val isDebit = entry.transaction.type == "DEBIT"
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTxForActions = entry.transaction },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (entry.transaction.description.isNotBlank()) entry.transaction.description else (if (isDebit) "Debit / You Gave" else "Credit / You Received"),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${DateUtils.formatDateTime(entry.transaction.date)} • ${entry.transaction.paymentMode}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NeutralGray
                                    )
                                    Text(
                                        text = "Balance: ${CurrencyFormatter.format(entry.runningBalance)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeutralGray.copy(alpha = 0.8f)
                                    )
                                }

                                Text(
                                    text = (if (isDebit) "-" else "+") + CurrencyFormatter.format(entry.transaction.amount),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDebit) DebitRed else CreditGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Entry Dialog
    if (showEntryDialogForType != null) {
        val type = showEntryDialogForType!!
        var amountStr by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var paymentMode by remember { mutableStateOf("Cash") }

        AlertDialog(
            onDismissRequest = { showEntryDialogForType = null },
            title = {
                Text(
                    text = if (type == "DEBIT") "Debit / You Give (₹)" else "Credit / You Receive (₹)",
                    fontWeight = FontWeight.Bold,
                    color = if (type == "DEBIT") DebitRed else CreditGreen
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
                        label = { Text("Description / Remarks (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )

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
                            viewModel.addTransaction(type, amt, description, paymentMode)
                            showEntryDialogForType = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (type == "DEBIT") DebitRed else CreditGreen)
                ) {
                    Text("Save Entry")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEntryDialogForType = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Transaction Actions Sheet / Dialog
    if (selectedTxForActions != null) {
        val tx = selectedTxForActions!!
        AlertDialog(
            onDismissRequest = { selectedTxForActions = null },
            title = { Text("Transaction Options") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            viewModel.convertTransactionType(tx.id)
                            selectedTxForActions = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Convert to ${if (tx.type == "DEBIT") "Credit / Receive" else "Debit / Give"}")
                    }

                    TextButton(
                        onClick = {
                            viewModel.duplicateTransaction(tx.id)
                            selectedTxForActions = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Make Duplicate Entry")
                    }

                    TextButton(
                        onClick = {
                            viewModel.deleteTransaction(tx.id)
                            selectedTxForActions = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = DebitRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Move to Recycle Bin", color = DebitRed)
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

    if (showUpiQrDialog) {
        val collectAmount = (balanceResult?.displayAmount ?: 0.0)
        com.mdesigns.khata.feature.components.UpiPaymentQrDialog(
            vpa = businessProfile?.email?.let { if (it.contains("@")) it else "" } ?: "",
            merchantName = businessProfile?.name ?: "M Designs Business",
            amount = collectAmount,
            customerName = person?.name ?: "Customer",
            onDismiss = { showUpiQrDialog = false }
        )
    }
}
