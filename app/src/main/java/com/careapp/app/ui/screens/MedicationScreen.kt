package com.careapp.app.ui.screens

import android.app.TimePickerDialog
import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.careapp.app.data.SampleData
import com.careapp.app.ui.components.MedicationCard
import com.careapp.app.ui.components.CareSectionCard
import com.careapp.app.ui.theme.*
import com.careapp.app.viewmodel.MedicationViewModel
import com.careapp.app.util.PdfExportManager
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MedicationScreen(viewModel: MedicationViewModel? = null, navController: NavController? = null, isCaregiverMode: Boolean = false) {
    // Use ViewModel if available, otherwise fall back to SampleData
    val medications by (viewModel?.medications?.collectAsState() ?: remember { mutableStateOf(SampleData.medications) })
    val showDialog by (viewModel?.showDialog?.collectAsState() ?: remember { mutableStateOf(false) })
    val dialogTitle by (viewModel?.dialogTitle?.collectAsState() ?: remember { mutableStateOf("") })
    val dialogMessage by (viewModel?.dialogMessage?.collectAsState() ?: remember { mutableStateOf("") })
    val showDuplicateWarning by (viewModel?.showDuplicateWarning?.collectAsState() ?: remember { mutableStateOf(false) })
    val duplicateWarningTime by (viewModel?.duplicateWarningTime?.collectAsState() ?: remember { mutableStateOf("") })
    val overdueMedications by (viewModel?.overdueMedications?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    val context = LocalContext.current
    var pendingMedication by remember { mutableStateOf<com.careapp.app.data.Medication?>(null) }
    val sortedMedications = medications.sortedBy { medicationMinutesUntilNow(it.scheduledTime) }

    pendingMedication?.let { medication ->
        val now = LocalTime.now()
        DisposableEffect(medication) {
            val picker = TimePickerDialog(
                context,
                { _, hour, minute ->
                    val selectedTime = LocalTime.of(hour, minute).format(DateTimeFormatter.ofPattern("HH:mm"))
                    viewModel?.takeMedication(medication.id, medication, selectedTime)
                    pendingMedication = null
                },
                now.hour,
                now.minute,
                DateFormat.is24HourFormat(context)
            )
            picker.setTitle("When did you take ${medication.name}?")
            picker.setOnCancelListener { pendingMedication = null }
            picker.show()
            onDispose { picker.dismiss() }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 112.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Text(
                text = if (isCaregiverMode) "Medication management" else "Today's medicine",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (isCaregiverMode) {
            item {
                CareSectionCard("Medication setup") {
                    Text("Instructions shown here are entered by the caregiver. CareApp does not recommend doses.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = { navController?.navigate(com.careapp.app.ui.CareAppScreen.CareSetup.name) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Add or update medications") }
                }
            }
        }

        if (overdueMedications.isNotEmpty()) {
            item {
                AlertCard("Medication overdue: ${overdueMedications.first().name} was due at ${overdueMedications.first().scheduledTime}.\nNeeds caregiver review.")
            }
        }

        item {
            CareSectionCard(if (isCaregiverMode) "Medication schedule" else "Today's Schedule") {
                sortedMedications.forEach { medication ->
                    MedicationCard(
                        medication = medication,
                        onTakeMedicine = { pendingMedication = medication },
                        onRemindLater = { viewModel?.remindLater(medication.id, medication) },
                        onNeedHelp = { viewModel?.needHelp(medication.id, medication) }
                    )
                }
            }
        }

        item {
            androidx.compose.material3.OutlinedButton(
                onClick = { navController?.navigate(com.careapp.app.ui.CareAppScreen.MedicationHistory.name) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) { Text("View History", fontSize = 17.sp) }
        }

        item {
            androidx.compose.material3.OutlinedButton(
                onClick = {
                    val text = medications.joinToString("\n") { "${it.name} ${it.dose} — ${it.scheduledTime} — ${it.instructions}" }
                    val file = PdfExportManager.createTextPdf(context, "careapp-medication-list.pdf", "CareApp Medication List", text)
                    PdfExportManager.sharePdf(context, file, "Share Medication List PDF")
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) { Text("Export Medication List PDF", fontSize = 16.sp) }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Dialog: Confirmation/Success
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel?.dismissDialog() },
            title = { Text(dialogTitle, fontWeight = FontWeight.Bold) },
            text = { Text(dialogMessage, fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = { viewModel?.dismissDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTealLight)
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Dialog: Duplicate Warning
    if (showDuplicateWarning) {
        AlertDialog(
            onDismissRequest = { viewModel?.dismissDuplicateWarning() },
            title = { Text("Already Taken", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This medication was already marked taken at $duplicateWarningTime.\n\n" +
                    "Ask your caregiver before taking it again.",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel?.dismissDuplicateWarning() },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
                ) {
                    Text("I Understand", color = TextDark, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

private fun medicationMinutesUntilNow(scheduledTime: String): Int {
    val parsed = runCatching {
        LocalTime.parse(scheduledTime.uppercase(), DateTimeFormatter.ofPattern("h:mm a"))
    }.getOrNull() ?: return Int.MAX_VALUE
    val now = LocalTime.now()
    val currentMinutes = now.hour * 60 + now.minute
    val scheduledMinutes = parsed.hour * 60 + parsed.minute
    return (scheduledMinutes - currentMinutes + 24 * 60) % (24 * 60)
}

@Composable
private fun AlertCard(text: String) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Text(text, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
    }
}
