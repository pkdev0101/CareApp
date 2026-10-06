package com.careapp.app.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.NavController
import com.careapp.app.data.*
import com.careapp.app.data.repository.CareRepository
import com.careapp.app.util.NotificationHelper
import com.careapp.app.util.CaregiverAuditStore
import com.careapp.app.util.MedicationReminderPreferences
import kotlinx.coroutines.launch

@Composable
fun CareSetupScreen(repository: CareRepository, navController: NavController? = null) {
    val context = LocalContext.current
    val auditStore = remember { CaregiverAuditStore(context) }
    val reminderPreferences = remember { MedicationReminderPreferences(context) }
    val scope = rememberCoroutineScope()
    var dialog by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var remindersEnabled by remember { mutableStateOf(reminderPreferences.isGloballyEnabled()) }
    var timeMenuExpanded by remember { mutableStateOf(false) }
    var validationMessage by remember { mutableStateOf("") }
    var savedMessage by remember { mutableStateOf("") }
    val notificationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        savedMessage = if (granted) "Medication reminders enabled." else "Notifications were not enabled. You can allow them in Android Settings."
    }

    fun open(type: String) {
        dialog = type
        name = ""
        detail = ""
        time = ""
        timeMenuExpanded = false
        validationMessage = ""
    }

    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)) {
        item { Text("Care Setup", style = MaterialTheme.typography.headlineMedium); Text("Caregivers enter the information Senior Mode uses.", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 12.dp)) }
        item { SetupCard("Caregiver Identity & Security Code", "Required before changes are logged") { open("identity") } }
        item {
            Card(Modifier.fillMaxWidth().padding(vertical = 5.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) { Text("Medication Reminders", style = MaterialTheme.typography.titleLarge); Text(if (remindersEnabled) "Enabled for all scheduled medications" else "Disabled", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        Switch(checked = remindersEnabled, onCheckedChange = { enabled -> remindersEnabled = enabled; reminderPreferences.setGloballyEnabled(enabled); scope.launch { repository.getAllMedicationsSync().forEach { if (enabled) NotificationHelper.scheduleMedicationReminder(context, it) else NotificationHelper.cancelMedicationReminder(context, it) } }; if (enabled && Build.VERSION.SDK_INT >= 33) notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS); savedMessage = if (enabled) "Medication reminders enabled." else "Medication reminders disabled." })
                    }
                    Text("Reminders use each medication's start time and interval.", style = MaterialTheme.typography.bodyMedium)
                    OutlinedButton(onClick = { if (Build.VERSION.SDK_INT >= 33) notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS); NotificationHelper.showTestMedicationReminder(context); savedMessage = "Test reminder sent. Check the device notification shade." }, modifier = Modifier.fillMaxWidth()) { Text("Send Test Reminder") }
                }
            }
        }
        item { SetupCard("Manage Medications", "Add, edit, remove, and schedule medications") { navController?.navigate(com.careapp.app.ui.CareAppScreen.ManageMedications.name) } }
        item { SetupCard("Manage People", "Add, edit, or remove caregivers, family, doctors, and phone numbers") { navController?.navigate(com.careapp.app.ui.CareAppScreen.ManagePeople.name) } }
        item { SetupCard("Manage Routines", "Add, edit, or remove morning, afternoon, or evening steps") { navController?.navigate(com.careapp.app.ui.CareAppScreen.ManageRoutines.name) } }
        item { SetupCard("Manage Places", "Add, edit, or remove Home, doctor offices, and safe locations") { navController?.navigate(com.careapp.app.ui.CareAppScreen.ManagePlaces.name) } }
        item { SetupCard("Emergency Profile", "Edit memory note, emergency contact, allergies, conditions, and care instructions") { navController?.navigate(com.careapp.app.ui.CareAppScreen.EmergencyProfile.name) } }
        item { SetupCard("App Preferences", "Text size, contrast, dark mode, and mode switching") { navController?.navigate(com.careapp.app.ui.CareAppScreen.Settings.name) } }
        if (savedMessage.isNotBlank()) item { Text(savedMessage, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 12.dp)) }
    }

    dialog?.let { type ->
        val title = when (type) { "identity" -> "Caregiver Identity"; "medication" -> "Add Medication"; "person" -> "Add Person"; "routine" -> "Add Routine Step"; "place" -> "Add Place"; else -> "Emergency Profile" }
        val valid = name.isNotBlank() && detail.isNotBlank() && (type != "medication" || time.isNotBlank())
        AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text(title) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(name, { name = it; validationMessage = "" }, label = { Text(if (type == "identity") "Caregiver name *" else if (type == "medication") "Medication name *" else "Name or title *") }, singleLine = true, isError = name.isBlank())
                    if (type == "identity") {
                        OutlinedTextField(detail, { detail = it; validationMessage = "" }, label = { Text("Security code *") }, singleLine = true, isError = detail.isBlank())
                        Text("This local code is logged with caregiver changes. It is not account authentication.", fontSize = 13.sp, color = Color.Gray)
                    } else if (type == "medication") {
                        OutlinedTextField(detail, { detail = it; validationMessage = "" }, label = { Text("Dose and instructions *") }, isError = detail.isBlank())
                        Box {
                            OutlinedTextField(
                                value = time,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Scheduled time *") },
                                placeholder = { Text("Choose a 15-minute time") },
                                isError = time.isBlank(),
                                trailingIcon = { TextButton(onClick = { timeMenuExpanded = true }) { Text("Choose") } },
                                modifier = Modifier.fillMaxWidth().clickable { timeMenuExpanded = true }
                            )
                            DropdownMenu(
                                expanded = timeMenuExpanded,
                                onDismissRequest = { timeMenuExpanded = false },
                                modifier = Modifier.heightIn(max = 320.dp)
                            ) {
                                medicationTimeSlots().forEach { slot ->
                                    DropdownMenuItem(
                                        text = { Text(slot) },
                                        onClick = { time = slot; timeMenuExpanded = false; validationMessage = "" }
                                    )
                                }
                            }
                        }
                        Text("Reminders are controlled globally in Care Setup. This medication will follow that setting.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else if (type == "routine") {
                        OutlinedTextField(detail, { detail = it; validationMessage = "" }, label = { Text("Time/category * (e.g. Morning 8:30 AM)") }, isError = detail.isBlank())
                    } else {
                        OutlinedTextField(detail, { detail = it; validationMessage = "" }, label = { Text(if (type == "emergency") "Details * (notes, contact, allergies, or conditions)" else "Phone, address, or notes *") }, isError = detail.isBlank())
                    }
                    if (validationMessage.isNotBlank()) Text(validationMessage, color = Color(0xFF9B2C2C), fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (!valid) { validationMessage = "Complete all required fields before saving."; return@Button }
                    scope.launch {
                        if (type != "identity" && !auditStore.hasIdentity()) {
                            validationMessage = "Set Caregiver Identity & Security Code before saving changes."
                            return@launch
                        }
                        when (type) {
                            "identity" -> auditStore.configureCaregiver(name, detail)
                            "medication" -> {
                                val medication = Medication(name = name, dose = detail, scheduledTime = time, instructions = detail, pillDescription = "", reason = "", doctor = "")
                                val savedMedication = repository.insertMedication(medication)
                                reminderPreferences.setEnabled(savedMedication, true)
                                if (reminderPreferences.isGloballyEnabled()) NotificationHelper.scheduleMedicationReminder(context, savedMedication)
                                else NotificationHelper.cancelMedicationReminder(context, savedMedication)
                                auditStore.record("Medication entered: ${medication.name}")
                            }
                            "person" -> { repository.insertMemoryCard(MemoryCard(name = name, relationship = "Caregiver", memoryCue = detail, helpsWith = "", phone = detail)); auditStore.record("Person entered: $name") }
                            "routine" -> { repository.insertRoutineTask(RoutineTask(title = name, category = detail.substringBefore(" "), suggestedTime = detail.substringAfter(" ", "Not scheduled"))); auditStore.record("Routine entered: $name") }
                            "place" -> auditStore.record("Place entry saved: $name")
                            "emergency" -> auditStore.record("Emergency profile updated")
                        }
                        savedMessage = if (type == "place" || type == "emergency") "$title saved for this demo session." else "$title saved."
                        dialog = null
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { dialog = null }) { Text("Cancel") } }
        )
    }
}

private fun medicationTimeSlots(): List<String> = buildList {
    for (hour in 0..23) for (minute in 0 until 60 step 15) {
        val suffix = if (hour < 12) "AM" else "PM"
        val displayHour = when (val h = hour % 12) { 0 -> 12; else -> h }
        add("%d:%02d %s".format(displayHour, minute, suffix))
    }
}

@Composable
private fun SetupCard(title: String, description: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(description, style = MaterialTheme.typography.bodyMedium)
            Text("Open", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 6.dp))
        }
    }
}
