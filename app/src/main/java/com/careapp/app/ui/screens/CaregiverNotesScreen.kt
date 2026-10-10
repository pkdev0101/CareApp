package com.careapp.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.careapp.app.ui.theme.*
import com.careapp.app.viewmodel.CaregiverNotesViewModel
import com.careapp.app.util.PdfExportManager
import com.careapp.app.util.CaregiverAuditStore
import com.careapp.app.ui.components.CareSectionCard

@Composable
fun CaregiverNotesScreen(viewModel: CaregiverNotesViewModel? = null) {
    val context = LocalContext.current
    val auditStore = remember { CaregiverAuditStore(context) }
    var auditSummary by remember { mutableStateOf(auditStore.handoffSummary()) }
    val note by (viewModel?.noteContent?.collectAsState() ?: remember { mutableStateOf("") })
    val mood by (viewModel?.mood?.collectAsState() ?: remember { mutableStateOf("Calm") })
    val sleep by (viewModel?.sleepQuality?.collectAsState() ?: remember { mutableStateOf("Good") })
    val summary by (viewModel?.summaryText?.collectAsState() ?: remember { mutableStateOf("") })
    val showDialog by (viewModel?.showDialog?.collectAsState() ?: remember { mutableStateOf(false) })
    val dialogMessage by (viewModel?.dialogMessage?.collectAsState() ?: remember { mutableStateOf("") })
    var breakfast by remember { mutableStateOf(false) }
    var lunch by remember { mutableStateOf(false) }
    var dinner by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel?.refreshTodayNote()
        auditSummary = auditStore.handoffSummary()
    }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Caregiver Handoff", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground) }
        item { Text("Record a few simple observations for the next caregiver.", fontSize = 18.sp) }
        item { CareSectionCard("Meals") { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { MealCheck("Breakfast", breakfast) { breakfast = !breakfast; viewModel?.toggleBreakfast() }; MealCheck("Lunch", lunch) { lunch = !lunch; viewModel?.toggleLunch() }; MealCheck("Dinner", dinner) { dinner = !dinner; viewModel?.toggleDinner() } } } }
        item { CareSectionCard("Notes") { OutlinedTextField(value = note, onValueChange = { viewModel?.updateNoteContent(it) }, modifier = Modifier.fillMaxWidth(), minLines = 4, label = { Text("Notes") }) } }
        item {
            CareSectionCard("Mood: $mood") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(listOf("Calm", "Happy"), listOf("Worried", "Tired")).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { value ->
                        FilterChip(
                            selected = mood == value,
                            onClick = { viewModel?.setMood(value) },
                            modifier = Modifier.heightIn(min = 48.dp),
                            label = { Text(value, maxLines = 1) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                            )
                            }
                        }
                    }
                }
            }
        }
        item {
            CareSectionCard("Sleep: $sleep") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Good", "Okay", "Poor").forEach { value ->
                        FilterChip(
                            selected = sleep == value,
                            onClick = { viewModel?.setSleepQuality(value) },
                            label = { Text(value, maxLines = 1) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }
        item {
            CareSectionCard("Handoff actions") {
                Button(
                    onClick = {
                        viewModel?.saveNote()
                        auditStore.record("Caregiver handoff note updated")
                        auditSummary = auditStore.handoffSummary()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Save Handoff Note") }
                OutlinedButton(onClick = { viewModel?.generateHandoffSummary() }, modifier = Modifier.fillMaxWidth()) { Text("Generate Handoff Summary") }
                OutlinedButton(
                    onClick = {
                        val handoff = "Notes: $note\nMood: $mood\nSleep: $sleep\nSummary: $summary\n\nCaregiver audit trail:\n$auditSummary"
                        val file = PdfExportManager.createTextPdf(context, "careapp-caregiver-handoff.pdf", "CareApp Caregiver Handoff", handoff)
                        PdfExportManager.sharePdf(context, file, "Share Caregiver Handoff PDF")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Export Handoff PDF") }
            }
        }
        if (summary.isNotBlank()) item { CareSectionCard("Generated summary") { Text(summary, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface) } }
        item {
            CareSectionCard("Caregiver audit trail") {
                Text("Recent changes include caregiver name and the last four digits of the local security code.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(auditSummary, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
    if (showDialog) AlertDialog(onDismissRequest = { viewModel?.dismissDialog() }, title = { Text("Saved") }, text = { Text(dialogMessage) }, confirmButton = { Button(onClick = { viewModel?.dismissDialog() }) { Text("OK") } })
}

@Composable
private fun MealCheck(label: String, checked: Boolean, onClick: () -> Unit) {
    Column { Checkbox(checked = checked, onCheckedChange = { onClick() }); Text(label, fontSize = 14.sp) }
}
