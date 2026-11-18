package com.mdesigns.khata.feature.more.denomination

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.mdesigns.khata.core.theme.PrimaryTeal
import com.mdesigns.khata.core.utils.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DenominationScreen(
    onNavigateBack: () -> Unit
) {
    val denominations = listOf(500, 200, 100, 50, 20, 10, 5)
    val counts = remember { mutableStateMapOf<Int, String>() }

    val grandTotal = denominations.sumOf { denom ->
        val count = counts[denom]?.toIntOrNull() ?: 0
        denom * count
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cash Denomination Counter") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { counts.clear() }) {
                        Text("Reset", color = PrimaryTeal)
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Total Cash:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = CurrencyFormatter.format(grandTotal.toDouble()),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(denominations.size) { index ->
                val denom = denominations[index]
                val count = counts[denom]?.toIntOrNull() ?: 0
                val total = denom * count

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹$denom",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(70.dp)
                        )
                        Text(text = "x", modifier = Modifier.padding(horizontal = 8.dp))
                        OutlinedTextField(
                            value = counts[denom] ?: "",
                            onValueChange = { counts[denom] = it },
                            placeholder = { Text("0") },
                            modifier = Modifier.width(100.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = MaterialTheme.shapes.small
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "= ${CurrencyFormatter.format(total.toDouble())}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryTeal
                        )
                    }
                }
            }
        }
    }
}
