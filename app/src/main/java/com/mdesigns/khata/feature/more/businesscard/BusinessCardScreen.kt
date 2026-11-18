package com.mdesigns.khata.feature.more.businesscard

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

    val upiString = remember(profile) {
        val email = profile?.email ?: ""
        val vpa = if (email.contains("@")) email else "mdesigns@upi"
        com.mdesigns.khata.core.utils.UpiHelper.createUpiUriString(
            vpa = vpa,
            name = profile?.name ?: "M Designs Business",
            amount = 0.0,
            note = "Business Card Contact & Pay"
        )
    }
    val qrBitmap = remember(upiString) {
        com.mdesigns.khata.core.utils.UpiHelper.generateQrBitmap(upiString, 256)
    }

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
                    .height(230.dp),
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
                        Column(modifier = Modifier.weight(1f)) {
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
                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "UPI QR Code",
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(3.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
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
                    val cardText = "Business Visiting Card\n${profile?.name ?: "M Designs Business"}\nContact: ${profile?.mobile}\nWebsite: https://mdesignsrmd.com"
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
