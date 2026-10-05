package com.careapp.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careapp.app.data.SampleData
import com.careapp.app.ui.theme.*

@Composable
fun CareSnapshotScreen() {
    val context = LocalContext.current
    var showFullView by remember { mutableStateOf(false) }
    val senior = SampleData.seniorProfile
    val medications = SampleData.medications

    fun shareSnapshot() {
        try {
            val snapshot = """
                CARE SNAPSHOT - ${senior.fullName}

                EMERGENCY INFORMATION
                Name: ${senior.preferredName}
                Memory Status: ${senior.memoryNote}
                Primary Caregiver: ${senior.primaryCaregiverName}
                Phone: ${senior.primaryCaregiverPhone}

                MEDICAL INFORMATION
                Allergies: ${senior.allergies}
                Conditions: ${senior.medicalConditions}
                Doctor: ${senior.doctorName}
                Hospital: ${senior.preferredHospital}

                MEDICATIONS
                ${medications.take(3).joinToString("\n") { "• ${it.name} ${it.dose} - ${it.scheduledTime}" }}

                CARE INSTRUCTIONS
                Approach: ${senior.calmingInstructions}
                Communication: Speak slowly and use simple words
            """.trimIndent()

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, snapshot)
                putExtra(Intent.EXTRA_SUBJECT, "Care Snapshot - ${senior.fullName}")
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(intent, "Share Snapshot"))
        } catch (e: Exception) {
            // Share not available
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Care Snapshot",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal
            )
            Button(
                onClick = { showFullView = !showFullView },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTealLight)
            ) {
                Text(if (showFullView) "Basic" else "Full")
            }
        }

        // Emergency Info
        SnapshotCard(
            title = "🚨 Emergency Information",
            items = listOf(
                "Name" to senior.preferredName,
                "Memory Status" to senior.memoryNote,
                "Primary Caregiver" to senior.primaryCaregiverName,
                "Phone" to senior.primaryCaregiverPhone
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Medical Info
        SnapshotCard(
            title = "⚕️ Medical Information",
            items = listOf(
                "Allergies" to senior.allergies,
                "Conditions" to senior.medicalConditions,
                "Doctor" to senior.doctorName,
                "Hospital" to senior.preferredHospital
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Medications
        SnapshotCard(
            title = "💊 Current Medications",
            items = medications.take(3).map { it.name to "${it.dose} - ${it.scheduledTime}" }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Care Instructions
        SnapshotCard(
            title = "📋 Care Instructions",
            items = listOf(
                "Approach" to senior.calmingInstructions,
                "Communication" to "Speak slowly and use simple words"
            )
        )

        if (showFullView) {
            Spacer(modifier = Modifier.height(12.dp))

            SnapshotCard(
                title = "📍 Address",
                items = listOf("Home" to senior.address)
            )

            Spacer(modifier = Modifier.height(12.dp))

            SnapshotCard(
                title = "📅 Important Dates",
                items = listOf("Birthday" to senior.birthday)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Share Button
        Button(
            onClick = { shareSnapshot() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SecondaryCoral)
        ) {
            Text("Share Snapshot", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun SnapshotCard(title: String, items: List<Pair<String, String>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = value.ifEmpty { "Not set" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (value.isEmpty()) Color.LightGray else TextDark,
                        modifier = Modifier.weight(1f)
                    )
                }
                Divider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
            }
        }
    }
}
