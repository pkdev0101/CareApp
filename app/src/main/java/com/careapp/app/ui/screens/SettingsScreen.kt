package com.careapp.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    var aboutTaps by remember { mutableIntStateOf(0) }
    var showPinDialog by remember { mutableStateOf(false) }
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var pinMessage by remember { mutableStateOf("") }

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
            item { Text("The top-right mode button returns to Senior Mode and takes you back to Home.", fontSize = 14.sp, color = TextGray) }
            item { SettingsActionCard("Change Caregiver PIN", "Requires the current master PIN. The new PIN must contain 4–6 digits.") { currentPin = ""; newPin = ""; pinMessage = ""; showPinDialog = true } }
        }
        item { SectionLabel("Accessibility") }
        item {
            SettingsCard("Text size") {
                Text("Current: ${(textSizeScale * 100).toInt()}%", fontSize = 15.sp, color = TextGray)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Normal" to 1f, "Large" to 1.2f, "Extra large" to 1.4f).forEach { (label, scale) ->
                        Button(onClick = { viewModel?.setTextSize(scale); auditStore.record("Text size changed to $label") }, modifier = Modifier.weight(1f).heightIn(min = 48.dp), colors = ButtonDefaults.buttonColors(containerColor = if (textSizeScale == scale) PrimaryTeal else MaterialTheme.colorScheme.surfaceVariant, contentColor = if (textSizeScale == scale) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)) {
                            Text(label, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        item {
            SettingsCard("Appearance") {
                Text("Use the device setting or choose a fixed theme for every CareApp screen.", color = TextGray, fontSize = 14.sp)
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
                modifier = Modifier.fillMaxWidth().clickable {
                    aboutTaps++
                    if (aboutTaps >= 5 && isCaregiverMode) {
                        aboutTaps = 0
                        navController?.navigate(CareAppScreen.DemoMode.name)
                    }
                },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("About CareApp", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text("Version 1.0", color = TextGray)
                    Text("A memory-care safety app for seniors and caregivers.", modifier = Modifier.padding(top = 4.dp))
                    if (isCaregiverMode && aboutTaps in 1..4) Text("About CareApp", color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
        item { Text("Demo Mode is available to caregivers by tapping About CareApp five times.", fontSize = 13.sp, color = TextGray) }
    }
    if (showPinDialog) AlertDialog(
        onDismissRequest = { showPinDialog = false },
        title = { Text("Change caregiver PIN") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("The current PIN is the master PIN for entering Caregiver Mode.")
            OutlinedTextField(currentPin, { currentPin = it.filter(Char::isDigit).take(6) }, label = { Text("Current master PIN") }, singleLine = true)
            OutlinedTextField(newPin, { newPin = it.filter(Char::isDigit).take(6) }, label = { Text("New caregiver PIN") }, singleLine = true)
            if (pinMessage.isNotBlank()) Text(pinMessage, color = MaterialTheme.colorScheme.error)
        } },
        confirmButton = { Button(onClick = { if (!pinStore.matches(currentPin)) pinMessage = "Current master PIN is incorrect." else if (newPin.length !in 4..6) pinMessage = "New PIN must contain 4 to 6 digits." else { pinStore.setPin(newPin); auditStore.record("Caregiver master PIN changed"); showPinDialog = false } }) { Text("Save PIN") } },
        dismissButton = { TextButton(onClick = { showPinDialog = false }) { Text("Cancel") } }
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 6.dp))
}

@Composable
private fun SettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = { Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface); content() })
    }
}

@Composable
private fun SettingsActionCard(title: String, description: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
            Text(description, color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(top = 4.dp))
            Text("Open Care Setup", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
