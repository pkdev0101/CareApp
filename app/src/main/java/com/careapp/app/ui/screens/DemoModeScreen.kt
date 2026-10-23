package com.careapp.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.careapp.app.ui.CareAppScreen
import com.careapp.app.ui.theme.*
import com.careapp.app.util.CaregiverPinStore
import com.careapp.app.util.DemoModeController
import com.careapp.app.data.repository.CareRepository
import com.careapp.app.util.PatientProfileData
import com.careapp.app.util.PatientProfileStore
import com.careapp.app.util.EmergencyProfileData
import com.careapp.app.util.EmergencyProfileStore
import kotlinx.coroutines.launch

private data class DemoStep(val title: String, val description: String, val route: CareAppScreen)

@Composable
fun DemoModeScreen(navController: NavController? = null) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val active = DemoModeController.isActive(context)
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
    val step = steps[current]

    LazyColumn(modifier = Modifier.fillMaxSize().background(BackgroundLight).padding(16.dp)) {
        item {
            Text("CareApp Demo Mode", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
            Text("Demo data for Congressional App Challenge", color = Color(0xFF6D4C41), modifier = Modifier.padding(vertical = 8.dp))
            Text("Step ${current + 1} of ${steps.size}", fontSize = 16.sp, color = TextGray)
        }
        item {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp)) {
            Text("Isolated demo data", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("These values are shown for demonstration only. Do not use this display in production.", color = MaterialTheme.colorScheme.error)
                    Text("Robert Miller / Sarah Miller / fictional records", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
            }
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
            Button(
                onClick = {
                    scope.launch {
                        if (active) {
                            DemoModeController.exit(context)
                        } else {
                            DemoModeController.enter(context)
                            CareRepository.getInstance(context).seedDemoIfNeeded()
                            PatientProfileStore(context).save(PatientProfileData(name = "Robert", fullName = "Robert Miller", age = "", dateOfBirth = "", relationship = "Daughter", memoryNote = "I may need help remembering where I am.", communicationStyle = "Speak calmly and use short sentences."))
                            EmergencyProfileStore(context).save(EmergencyProfileData(memoryNote = "My name is Robert. I may need help remembering where I am. Please help me contact my caregiver.", allergies = "Demo only — not medical advice", conditions = "Demo only", emergencyContact = "Sarah Miller", preferredHospital = "Demo safe place", calmingInstructions = "Speak calmly, use short sentences, and remind Robert of the next step."))
                        }
                        (context as? android.app.Activity)?.recreate()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (active) "Exit Isolated Demo" else "Start Isolated Demo") }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { navController?.navigate(step.route.name) }, modifier = Modifier.fillMaxWidth()) { Text("Open Feature") }
            Spacer(Modifier.height(8.dp))
            if (active) {
                OutlinedButton(
                    onClick = { navController?.navigate(CareAppScreen.DemoOnboarding.name) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Replay Caregiver Setup") }
                Spacer(Modifier.height(8.dp))
            }
            Button(onClick = { if (current < steps.lastIndex) current++ else navController?.popBackStack() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SecondaryCoral)) { Text(if (current < steps.lastIndex) "Next" else "Finish Demo") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { navController?.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Skip Demo") }
        }
    }
}
