package com.careapp.app.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.careapp.app.data.CaregiverNote
import com.careapp.app.data.Medication
import com.careapp.app.data.MedicationLog
import com.careapp.app.data.SampleData
import com.careapp.app.data.repository.CareRepository
import com.careapp.app.ui.theme.*
import com.careapp.app.util.DoctorSummaryBuilder
import com.careapp.app.util.PdfExportManager

@Composable
fun DoctorSummaryScreen(navController: NavController? = null) {
    val context = LocalContext.current
    val repository = remember { CareRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    val senior = SampleData.seniorProfile
    var medications by remember { mutableStateOf<List<Medication>>(emptyList()) }
    var medicationLogs by remember { mutableStateOf<List<MedicationLog>>(emptyList()) }
    var caregiverNotes by remember { mutableStateOf<List<CaregiverNote>>(emptyList()) }

    var reportText by remember {
        mutableStateOf(
            DoctorSummaryBuilder.generateWeeklyReport(
                senior = senior,
                medications = medications,
                medicationLogs = medicationLogs,
                caregiverNotes = caregiverNotes
            )
        )
    }

    fun loadLatestReport() {
        scope.launch {
            val latestMedications = repository.getAllMedicationsSync()
            val latestLogs = repository.getAllMedicationLogs()
            val latestNotes = repository.getAllCaregiverNotes().sortedBy { it.date }
            medications = latestMedications
            medicationLogs = latestLogs
            caregiverNotes = latestNotes
            reportText = DoctorSummaryBuilder.generateWeeklyReport(
                senior = senior,
                medications = latestMedications,
                medicationLogs = latestLogs,
                caregiverNotes = latestNotes
            )
        }
    }

    LaunchedEffect(Unit) { loadLatestReport() }

    val medicationCounts = remember(medications, medicationLogs) {
        DoctorSummaryBuilder.calculateWeeklyMedicationCounts(medications, medicationLogs)
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
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Match the Caregiver Handoff layout: themed background, compact heading,
        // and content that starts immediately below the heading.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Weekly Care Report",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "For ${senior.doctorName}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
            )

            ReportSection("Patient") {
                ReportRow("Name", senior.fullName)
                ReportRow("Report period", "Last 7 days")
                ReportRow("Doctor", senior.doctorName)
            }
            ReportSection("Medication overview") {
                ReportRow("Medications listed", medications.size.toString())
                ReportRow("Expected doses", medicationCounts.expected.toString())
                ReportRow("Doses taken", "${medicationCounts.taken} recorded")
                ReportRow("Missed doses", "${medicationCounts.missed} recorded")
                Text(
                    "Medication instructions are caregiver-entered and are not dose recommendations.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ReportSection("Current medications") {
                medications.forEach { medication ->
                    Text(
                        medication.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "${medication.dose} · ${medication.scheduledTime}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }
            ReportSection("Caregiver observations") {
                val latest = caregiverNotes.maxByOrNull { it.date }
                ReportRow("Mood", latest?.mood ?: "Not recorded")
                ReportRow("Sleep", latest?.sleepQuality ?: "Not recorded")
                ReportRow("Safety", latest?.safetyNotes ?: "Not recorded")
                ReportRow("Confusion notes", latest?.confusionNotes ?: "None recorded")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Button(
                onClick = { shareReport() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary)
            ) {
                Text("📧 Share with Doctor", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("CareApp Doctor Summary", reportText))
                    Toast.makeText(context, "Summary copied", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
            ) {
                Text("📋 Copy to Clipboard", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    val file = PdfExportManager.createTextPdf(context, "careapp-doctor-summary.pdf", "CareApp Doctor Summary", reportText)
                    PdfExportManager.sharePdf(context, file, "Share Doctor Summary PDF")
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Export PDF") }

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(20.dp))

            // Disclaimer
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                border = androidx.compose.material3.CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "⚠️ Medical Disclaimer",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "This report is for information and caregiver communication only. It does not diagnose or treat medical conditions. Always consult with a healthcare provider for medical advice.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ReportSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            content()
        }
    }
}

@Composable
private fun ReportRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
