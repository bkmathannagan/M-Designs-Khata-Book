package com.mdesigns.khata.feature.more.holidays

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mdesigns.khata.core.theme.PrimaryTeal

data class HolidayItem(val name: String, val date: String, val day: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankHolidaysScreen(
    onNavigateBack: () -> Unit
) {
    val holidays = listOf(
        HolidayItem("Republic Day", "26 Jan", "Monday"),
        HolidayItem("Maha Shivratri", "15 Feb", "Sunday"),
        HolidayItem("Holi", "04 Mar", "Wednesday"),
        HolidayItem("Good Friday", "03 Apr", "Friday"),
        HolidayItem("Eid-ul-Fitr", "20 Apr", "Monday"),
        HolidayItem("Independence Day", "15 Aug", "Saturday"),
        HolidayItem("Ganesh Chaturthi", "14 Sep", "Monday"),
        HolidayItem("Gandhi Jayanti", "02 Oct", "Friday"),
        HolidayItem("Dussehra", "20 Oct", "Tuesday"),
        HolidayItem("Diwali", "08 Nov", "Sunday"),
        HolidayItem("Christmas", "25 Dec", "Friday")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bank Holidays Calendar") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(holidays) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = item.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(text = item.day, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(text = item.date, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    }
                }
            }
        }
    }
}
