package com.mdesigns.khata.feature.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdesigns.khata.core.theme.*
import com.mdesigns.khata.core.utils.CurrencyFormatter
import com.mdesigns.khata.core.utils.DateUtils
import com.mdesigns.khata.core.utils.KhataBalanceCalculator
import com.mdesigns.khata.domain.model.PersonSummary

@Composable
fun SummaryStatCard(
    title: String,
    amount: Double,
    caption: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.bodyMedium, color = NeutralGray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = CurrencyFormatter.format(amount),
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = caption, style = MaterialTheme.typography.labelSmall, color = NeutralGray)
        }
    }
}

@Composable
fun PersonSummaryItem(
    summary: PersonSummary,
    onClick: () -> Unit,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    val (statusColor, statusText) = when (summary.status) {
        KhataBalanceCalculator.BalanceStatus.YOU_COLLECT -> Pair(CreditGreen, "You Collect")
        KhataBalanceCalculator.BalanceStatus.YOU_GIVE -> Pair(DebitRed, "You Give")
        KhataBalanceCalculator.BalanceStatus.SETTLED -> Pair(NeutralGray, "Settled")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PrimaryTeal.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = summary.person.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal,
                        fontSize = 18.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = summary.person.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (summary.person.mobile.isNotEmpty()) {
                    Text(
                        text = summary.person.mobile,
                        style = MaterialTheme.typography.bodySmall,
                        color = NeutralGray
                    )
                }
                summary.lastTransactionDate?.let {
                    Text(
                        text = "Last: ${DateUtils.formatDate(it)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeutralGray.copy(alpha = 0.8f)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.format(summary.netBalance),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
fun EmptyStateMessage(
    title: String,
    subtitle: String,
    icon: ImageVector = Icons.Default.Inbox,
    actionButton: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryTeal.copy(alpha = 0.4f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = NeutralGray)
        if (actionButton != null) {
            Spacer(modifier = Modifier.height(20.dp))
            actionButton()
        }
    }
}
