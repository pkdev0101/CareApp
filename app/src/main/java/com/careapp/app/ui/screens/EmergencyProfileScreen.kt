package com.careapp.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.careapp.app.util.CaregiverAuditStore
import com.careapp.app.util.EmergencyProfileData
import com.careapp.app.util.EmergencyProfileStore

@Composable
fun EmergencyProfileScreen() {
    val context = LocalContext.current
    val store = remember { EmergencyProfileStore(context) }
    val audit = remember { CaregiverAuditStore(context) }
    val saved = remember { store.load() }
    var memoryNote by remember { mutableStateOf(saved.memoryNote) }
    var allergies by remember { mutableStateOf(saved.allergies) }
    var conditions by remember { mutableStateOf(saved.conditions) }
    var emergencyContact by remember { mutableStateOf(saved.emergencyContact) }
    var hospital by remember { mutableStateOf(saved.preferredHospital) }
    var calming by remember { mutableStateOf(saved.calmingInstructions) }
    var message by remember { mutableStateOf("") }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Emergency Profile", style = MaterialTheme.typography.headlineSmall); Text("This information is stored locally and is used for the emergency card and hand-off summary.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        item { ProfileField("Memory-loss note *", memoryNote) { memoryNote = it }; ProfileField("Allergies", allergies) { allergies = it }; ProfileField("Conditions", conditions) { conditions = it }; ProfileField("Emergency contact *", emergencyContact) { emergencyContact = it }; ProfileField("Preferred hospital or doctor", hospital) { hospital = it }; ProfileField("What calms me / what to avoid", calming) { calming = it } }
        item { Button(onClick = { if (memoryNote.isBlank() || emergencyContact.isBlank()) message = "Memory-loss note and emergency contact are required." else { store.save(EmergencyProfileData(memoryNote.trim(), allergies.trim(), conditions.trim(), emergencyContact.trim(), hospital.trim(), calming.trim())); audit.record("Emergency profile updated"); message = "Emergency profile saved." } }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Save Emergency Profile") }; if (message.isNotBlank()) Text(message, color = if (message.contains("required")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary) }
    }
}

@Composable private fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit) { OutlinedTextField(value, onValueChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth(), minLines = if (label.contains("note") || label.contains("calms")) 2 else 1) }
