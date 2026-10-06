package com.careapp.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.careapp.app.ui.CareAppScreen
import com.careapp.app.ui.theme.*
import com.careapp.app.viewmodel.AccessibilityViewModel
import com.careapp.app.viewmodel.ThemeViewModel
import androidx.compose.ui.platform.LocalContext
import com.careapp.app.util.CaregiverAuditStore
import com.careapp.app.util.CaregiverPinStore

@Composable
fun SettingsScreen(viewModel: AccessibilityViewModel? = null, navController: NavController? = null, isCaregiverMode: Boolean = false, themeMode: ThemeViewModel.AppThemeMode = ThemeViewModel.AppThemeMode.SYSTEM, onThemeModeChange: (ThemeViewModel.AppThemeMode) -> Unit = {}) {
    val textSizeScale by (viewModel?.textSizeScale?.collectAsState() ?: remember { mutableStateOf(1f) })
    val context = LocalContext.current
    val pinStore = remember { CaregiverPinStore(context) }
    val auditStore = remember { CaregiverAuditStore(context) }
    var showPinDialog by remember { mutableStateOf(false) }
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var pinMessage by remember { mutableStateOf("") }
    var showSupervisorPinDialog by remember { mutableStateOf(false) }
    var currentSupervisorPin by remember { mutableStateOf("") }
    var newSupervisorPin by remember { mutableStateOf("") }
    var supervisorPinMessage by remember { mutableStateOf("") }
    var showCurrentCaregiverPin by remember { mutableStateOf(false) }
    var showNewCaregiverPin by remember { mutableStateOf(false) }
    var showCurrentSupervisorPin by remember { mutableStateOf(false) }
    var showNewSupervisorPin by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
            Text("Adjust CareApp for the person using it.", style = MaterialTheme.typography.bodyLarge)
        }
        if (isCaregiverMode) {
            item { SectionLabel("Caregiver tools") }
            item {
                SettingsActionCard("Care Setup", "Manage medications, routines, people, places, reminders, and emergency information") {
                    navController?.navigate(CareAppScreen.CareSetup.name)
                }
            }
            item { Text("The top-right mode button returns to Senior Mode and takes you back to Home.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            item { SettingsActionCard("Change Caregiver PIN", "Requires the current master PIN. The new PIN must contain 4–6 digits.", "Change PIN") { currentPin = ""; newPin = ""; pinMessage = ""; showCurrentCaregiverPin = false; showNewCaregiverPin = false; showPinDialog = true } }
        }
        item { SettingsActionCard("Change Supervisor PIN", "Controls caregiver PIN recovery and caregiver access administration.", "Change Supervisor PIN") { currentSupervisorPin = ""; newSupervisorPin = ""; supervisorPinMessage = ""; showCurrentSupervisorPin = false; showNewSupervisorPin = false; showSupervisorPinDialog = true } }
        item { SectionLabel("Accessibility") }
        item {
            SettingsCard("Text size") {
                Text("Choose the letter size that feels most comfortable.", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Default" to 1f, "Larger" to 1.2f, "Largest" to 1.4f).forEach { (label, scale) ->
                        Button(onClick = { viewModel?.setTextSize(scale); auditStore.record("Text size changed to $label") }, modifier = Modifier.weight(1f).heightIn(min = 48.dp), colors = ButtonDefaults.buttonColors(containerColor = if (textSizeScale == scale) PrimaryTeal else MaterialTheme.colorScheme.surfaceVariant, contentColor = if (textSizeScale == scale) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("A", fontSize = (18f * scale).sp, fontWeight = FontWeight.Bold, lineHeight = (20f * scale).sp)
                            }
                        }
                    }
                }
            }
        }
        item {
            SettingsCard("Appearance") {
                Text("Use the device setting or choose a fixed theme for every CareApp screen.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("System default" to ThemeViewModel.AppThemeMode.SYSTEM, "Light" to ThemeViewModel.AppThemeMode.LIGHT, "Dark" to ThemeViewModel.AppThemeMode.DARK).forEach { (label, mode) ->
                        Button(onClick = { onThemeModeChange(mode); auditStore.record("Theme changed to $label") }, modifier = Modifier.weight(1f).heightIn(min = 48.dp), colors = ButtonDefaults.buttonColors(containerColor = if (themeMode == mode) PrimaryTeal else MaterialTheme.colorScheme.surfaceVariant, contentColor = if (themeMode == mode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)) {
                            Text(label, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        item { SectionLabel("About") }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("About CareApp", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text("Version 1.0", color = TextGray)
                    Text("A memory-care safety app for seniors and caregivers.", modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
        item { Text("Caregivers can open Demo Mode by tapping the CareApp title in the top-left corner 10 times.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
    if (showPinDialog) AlertDialog(
        onDismissRequest = { showPinDialog = false },
        title = { Text("Change caregiver PIN") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("The supervisor PIN authorizes caregiver PIN changes.")
            OutlinedTextField(currentPin, { currentPin = it.filter(Char::isDigit).take(6) }, label = { Text("Current supervisor PIN") }, visualTransformation = if (showCurrentCaregiverPin) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { TextButton(onClick = { showCurrentCaregiverPin = !showCurrentCaregiverPin }) { Text(if (showCurrentCaregiverPin) "Hide" else "Show") } }, singleLine = true)
            OutlinedTextField(newPin, { newPin = it.filter(Char::isDigit).take(6) }, label = { Text("New caregiver PIN") }, visualTransformation = if (showNewCaregiverPin) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { TextButton(onClick = { showNewCaregiverPin = !showNewCaregiverPin }) { Text(if (showNewCaregiverPin) "Hide" else "Show") } }, singleLine = true)
            if (pinMessage.isNotBlank()) Text(pinMessage, color = MaterialTheme.colorScheme.error)
        } },
        confirmButton = { Button(onClick = { if (!pinStore.matchesSupervisor(currentPin)) pinMessage = "Current supervisor PIN is incorrect." else if (newPin.length !in 4..6) pinMessage = "New PIN must contain 4 to 6 digits." else { pinStore.setCaregiverPin(newPin); auditStore.record("Caregiver PIN changed"); showPinDialog = false } }) { Text("Save PIN") } },
        dismissButton = { TextButton(onClick = { showPinDialog = false }) { Text("Cancel") } }
    )
    if (showSupervisorPinDialog) AlertDialog(
        onDismissRequest = { showSupervisorPinDialog = false },
        title = { Text("Change supervisor PIN") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("This PIN controls caregiver access recovery. Keep it with the supervisor.")
            OutlinedTextField(currentSupervisorPin, { currentSupervisorPin = it.filter(Char::isDigit).take(6) }, label = { Text("Current supervisor PIN") }, visualTransformation = if (showCurrentSupervisorPin) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { TextButton(onClick = { showCurrentSupervisorPin = !showCurrentSupervisorPin }) { Text(if (showCurrentSupervisorPin) "Hide" else "Show") } }, singleLine = true)
            OutlinedTextField(newSupervisorPin, { newSupervisorPin = it.filter(Char::isDigit).take(6) }, label = { Text("New supervisor PIN") }, visualTransformation = if (showNewSupervisorPin) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { TextButton(onClick = { showNewSupervisorPin = !showNewSupervisorPin }) { Text(if (showNewSupervisorPin) "Hide" else "Show") } }, singleLine = true)
            if (supervisorPinMessage.isNotBlank()) Text(supervisorPinMessage, color = MaterialTheme.colorScheme.error)
        } },
        confirmButton = { Button(onClick = { if (!pinStore.matchesSupervisor(currentSupervisorPin)) supervisorPinMessage = "Current supervisor PIN is incorrect." else if (newSupervisorPin.length !in 4..6) supervisorPinMessage = "New PIN must contain 4 to 6 digits." else { pinStore.setSupervisorPin(newSupervisorPin); auditStore.record("Supervisor PIN changed"); showSupervisorPinDialog = false } }) { Text("Save Supervisor PIN") } },
        dismissButton = { TextButton(onClick = { showSupervisorPinDialog = false }) { Text("Cancel") } }
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 6.dp))
}

@Composable
private fun SettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = { Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface); content() })
    }
}

@Composable
private fun SettingsActionCard(title: String, description: String, actionLabel: String = "Open Care Setup", onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(actionLabel, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
