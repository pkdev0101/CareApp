package com.careapp.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.careapp.app.ui.CareAppScreen
import com.careapp.app.ui.components.CareSectionCard
import com.careapp.app.ui.theme.*
import com.careapp.app.util.CaregiverPinStore

private data class DemoStep(val title: String, val description: String, val route: CareAppScreen)

@Composable
fun DemoModeScreen(navController: NavController? = null) {
    val steps = listOf(
        DemoStep("Orientation Board", "See the senior's name, routine, and caregiver contact.", CareAppScreen.Home),
        DemoStep("Medication Safety", "Review the schedule and overdue-dose safeguards.", CareAppScreen.Medication),
        DemoStep("Duplicate-dose Protection", "Try a medication action and see the safety confirmation.", CareAppScreen.Medication),
        DemoStep("Calm Mode", "Open Common Questions and the breathing support tools.", CareAppScreen.CalmMode),
        DemoStep("Lost Mode", "Show demo location, saved places, and safe-zone status.", CareAppScreen.Lost),
        DemoStep("Emergency Card", "Open local emergency information and safe demo actions.", CareAppScreen.Emergency),
        DemoStep("Caregiver Handoff", "Review notes that can be shared with the next caregiver.", CareAppScreen.CaregiverNotes),
        DemoStep("Doctor Summary", "View the local seven-day organization report.", CareAppScreen.DoctorSummary),
        DemoStep("Care Snapshot", "Share a one-page profile with a helper or doctor.", CareAppScreen.CareSnapshot)
    )
    var current by remember { mutableStateOf(0) }
    var showPins by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val pinStore = remember { CaregiverPinStore(context) }
    val step = steps[current]

    LazyColumn(modifier = Modifier.fillMaxSize().background(BackgroundLight).padding(16.dp)) {
        item {
            Text("CareApp Demo Mode", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
            Text("Demo data for Congressional App Challenge", color = Color(0xFF6D4C41), modifier = Modifier.padding(vertical = 8.dp))
            Text("Step ${current + 1} of ${steps.size}", fontSize = 16.sp, color = TextGray)
        }
        item {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(20.dp)) {
                    Text(step.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    Text(step.description, fontSize = 18.sp, color = TextDark, modifier = Modifier.padding(top = 12.dp))
                }
            }
        }
        item {
            Button(onClick = { navController?.navigate(step.route.name) }, modifier = Modifier.fillMaxWidth()) { Text("Open Feature") }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { if (current < steps.lastIndex) current++ else navController?.popBackStack() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SecondaryCoral)) { Text(if (current < steps.lastIndex) "Next" else "Finish Demo") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { navController?.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Skip Demo") }
        }
        item {
            CareSectionCard("Temporary PIN display") {
                Text("For local demo testing only. Do not use this screen to expose PINs in a real deployment.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                OutlinedButton(onClick = { showPins = !showPins }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (showPins) "Hide PINs" else "Show PINs temporarily")
                }
                if (showPins) {
                    Text("Supervisor PIN: ${pinStore.supervisorPin()}", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    Text("Caregiver PIN: ${pinStore.caregiverPin()}", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
