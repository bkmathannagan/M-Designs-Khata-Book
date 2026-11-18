package com.mdesigns.khata.feature.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mdesigns.khata.core.sharing.ShareManager
import com.mdesigns.khata.core.theme.CreditGreen
import com.mdesigns.khata.core.theme.PrimaryTeal
import com.mdesigns.khata.core.utils.CurrencyFormatter
import com.mdesigns.khata.core.utils.UpiHelper
import java.io.File
import java.io.FileOutputStream

@Composable
fun UpiPaymentQrDialog(
    vpa: String,
    merchantName: String,
    amount: Double,
    customerName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val effectiveVpa = vpa.ifBlank { "mdesigns@upi" }
    val upiUriString = remember(effectiveVpa, merchantName, amount, customerName) {
        UpiHelper.createUpiUriString(
            vpa = effectiveVpa,
            name = merchantName,
            amount = amount,
            note = "Payment from $customerName"
        )
    }

    val qrBitmap = remember(upiUriString) {
        UpiHelper.generateQrBitmap(upiUriString, 512)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Scan & Pay via UPI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = merchantName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "UPI ID: $effectiveVpa",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = CurrencyFormatter.format(amount),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = CreditGreen
                )

                Spacer(modifier = Modifier.height(16.dp))

                // QR Container
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "UPI Payment QR Code",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        CircularProgressIndicator(color = PrimaryTeal)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Accepts Google Pay, PhonePe, Paytm, BHIM & all UPI apps",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (qrBitmap != null) {
                                try {
                                    val cacheFile = File(context.cacheDir, "reports/upi_qr_${System.currentTimeMillis()}.png").apply {
                                        parentFile?.mkdirs()
                                    }
                                    val out = FileOutputStream(cacheFile)
                                    qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                                    out.flush()
                                    out.close()
                                    ShareManager.shareFile(context, cacheFile, "image/png", "Share UPI Payment QR")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share QR")
                    }

                    Button(
                        onClick = {
                            UpiHelper.launchUpiApp(context, upiUriString)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pay / Open")
                    }
                }
            }
        }
    }
}
