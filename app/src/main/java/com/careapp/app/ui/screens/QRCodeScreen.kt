package com.careapp.app.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careapp.app.data.SampleData
import com.careapp.app.ui.theme.*
import com.careapp.app.util.QRCodeGenerator

@Composable
fun QRCodeScreen() {
    val context = LocalContext.current
    val senior = SampleData.seniorProfile
    var privacyLevel by remember { mutableStateOf("basic") }

    // Generate QR code data
    val qrData = QRCodeGenerator.formatQRContent(senior, privacyLevel)

    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Generate QR code on composition
    LaunchedEffect(qrData) {
        qrBitmap = QRCodeGenerator.generateQRCode(qrData, 400)
    }

    fun shareQRCode() {
        try {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Emergency QR Code for ${senior.fullName}\n\n$qrData")
                putExtra(Intent.EXTRA_SUBJECT, "Emergency QR Code - ${senior.fullName}")
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(intent, "Share QR Code"))
        } catch (e: Exception) {
            // Share not available
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryTeal)
                .padding(16.dp)
        ) {
            Text(
                text = "🔍 Emergency QR Code",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Share with first responders or emergency contacts",
                fontSize = 14.sp,
                color = PrimaryTealVeryLight
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Privacy level", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                FilterChip(selected = privacyLevel == "basic", onClick = { privacyLevel = "basic" }, label = { Text("Basic") })
                FilterChip(selected = privacyLevel == "full", onClick = { privacyLevel = "full" }, label = { Text("Full") })
            }
            Text("Scan this to view emergency care information.", fontSize = 14.sp, color = TextGray)

            // QR Code Display
            Card(
                modifier = Modifier
                    .size(280.dp)
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                if (qrBitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = qrBitmap!!.asImageBitmap(),
                        contentDescription = "Emergency QR Code",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryTeal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Emergency Card & QR",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    InfoRow("Name:", senior.fullName)
                    InfoRow("Caregiver:", senior.primaryCaregiverName)
                    InfoRow("Phone:", senior.primaryCaregiverPhone)
                    InfoRow("Address:", senior.address)
                    InfoRow("Allergies:", senior.allergies)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Button(
                onClick = { shareQRCode() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryCoral)
            ) {
                Text("📤 Share QR Code", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { shareQRCode() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTealLight)
            ) {
                Text("🖨️ Share for Wallet", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Info Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3F2FD)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "💡 Tip",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Keep this QR code with you or in your wallet. First responders can scan it to access your emergency contact information instantly.",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextGray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value.ifEmpty { "Not set" },
            fontSize = 14.sp,
            color = TextDark,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
