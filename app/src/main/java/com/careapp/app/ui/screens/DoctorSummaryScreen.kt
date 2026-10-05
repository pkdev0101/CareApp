package com.careapp.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careapp.app.data.SampleData
import com.careapp.app.ui.theme.*
import com.careapp.app.util.DoctorSummaryBuilder

@Composable
fun DoctorSummaryScreen() {
    val context = LocalContext.current
    val senior = SampleData.seniorProfile
    val medications = SampleData.medications
    val caregiverNotes = listOf(SampleData.caregiverNote)

    var reportText by remember {
        mutableStateOf(
            DoctorSummaryBuilder.generateWeeklyReport(
                senior = senior,
                medications = medications,
                medicationLogs = emptyList(),
                caregiverNotes = caregiverNotes
            )
        )
    }

    fun shareReport() {
        try {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, reportText)
                putExtra(Intent.EXTRA_SUBJECT, "Weekly Care Report - ${senior.fullName}")
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(intent, "Share Report"))
        } catch (e: Exception) {
            // Share not available
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryTeal)
                .padding(16.dp)
        ) {
            Text(
                text = "📋 Weekly Care Report",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "For ${senior.doctorName}",
                fontSize = 14.sp,
                color = PrimaryTealVeryLight
            )
        }

        // Report Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Report Text Display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = reportText,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextDark,
                        modifier = Modifier.padding(16.dp),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Button(
                onClick = { shareReport() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryCoral)
            ) {
                Text("📧 Share with Doctor", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* Copy to clipboard */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTealLight)
            ) {
                Text("📋 Copy to Clipboard", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    // Regenerate report
                    reportText = DoctorSummaryBuilder.generateWeeklyReport(
                        senior = senior,
                        medications = medications,
                        medicationLogs = emptyList(),
                        caregiverNotes = caregiverNotes
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentAmber)
            ) {
                Text("🔄 Refresh Report", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Disclaimer
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                border = androidx.compose.material3.CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "⚠️ Medical Disclaimer",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "This report is for information and caregiver communication only. It does not diagnose or treat medical conditions. Always consult with a healthcare provider for medical advice.",
                        fontSize = 11.sp,
                        color = Color(0xFF5D4037)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
