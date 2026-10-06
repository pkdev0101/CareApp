package com.careapp.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.careapp.app.data.SampleData
import com.careapp.app.ui.CareAppScreen
import com.careapp.app.ui.components.LargeButton
import com.careapp.app.ui.theme.*
import kotlinx.coroutines.flow.StateFlow

@Composable
fun EmergencyScreen(viewModel: com.careapp.app.viewmodel.EmergencyViewModel? = null, navController: NavController? = null) {
    val senior = SampleData.seniorProfile
    val context = LocalContext.current
    val showDialog by (viewModel?.showDialog?.collectAsState() ?: remember { mutableStateOf(false) })
    val dialogTitle by (viewModel?.dialogTitle?.collectAsState() ?: remember { mutableStateOf("") })
    val dialogMessage by (viewModel?.dialogMessage?.collectAsState() ?: remember { mutableStateOf("") })
    val showCountdown by (viewModel?.showCountdown?.collectAsState() ?: remember { mutableStateOf(false) })
    val countdown by (viewModel?.countdownValue?.collectAsState() ?: remember { mutableStateOf(0) })
    val timeline by (viewModel?.emergencyTimeline?.collectAsState() ?: remember { mutableStateOf(emptyList()) })

    LaunchedEffect(showCountdown) {
        while (showCountdown) {
            delay(1000)
            viewModel?.decrementCountdown()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DangerRedLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Emergency Help",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DangerRed,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }

        item {
            Text(
                text = "Are you safe?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        item {
            LargeButton(
                text = "Call Caregiver\n${senior.primaryCaregiverName}",
                onClick = {
                    viewModel?.callCaregiver()
                    // Open phone dialer with caregiver number (no permission needed, never dials)
                    try {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = android.net.Uri.parse("tel:${senior.primaryCaregiverPhone}")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Silently fail if no dialer app (shouldn't happen)
                    }
                },
                backgroundColor = PrimaryTealLight
            )
        }

        item {
            LargeButton(
                text = "Call 911\nEmergency Services",
                onClick = { viewModel?.call911() },
                backgroundColor = DangerRed
            )
        }

        item {
            LargeButton(
                text = "Show My Emergency Card & QR",
                onClick = { navController?.navigate(CareAppScreen.QRCode.name) },
                backgroundColor = SecondaryCoral
            )
        }


        item {
            LargeButton(
                text = "I Am Lost",
                onClick = { viewModel?.locationViewed(); navController?.navigate(CareAppScreen.Lost.name) },
                backgroundColor = SupportPlum
            )
        }

        if (showCountdown) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = DangerRedLight)) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Help request in $countdown seconds", fontWeight = FontWeight.Bold)
                        Button(onClick = { viewModel?.cancelHelp() }) { Text("I'm OK — Cancel") }
                    }
                }
            }
        }

        item {
            LargeButton(
                text = "I Fell / Need Help",
                onClick = { viewModel?.needImmediateHelp() },
                backgroundColor = DangerRed
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Emergency Card Display (shown when button tapped)
        if (false) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DangerRedLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "My Emergency Card",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DangerRed
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "My name is ${senior.fullName}.",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = senior.memoryNote,
                            fontSize = 16.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        EmergencyCardRow("Caregiver", senior.primaryCaregiverName)
                        EmergencyCardRow("Phone", senior.primaryCaregiverPhone)

                        Spacer(modifier = Modifier.height(8.dp))

                        EmergencyCardRow("Allergies", senior.allergies)
                        EmergencyCardRow("Medications", "Donepezil, Metformin, Lisinopril")
                        EmergencyCardRow("Doctor", senior.doctorName)
                        EmergencyCardRow("Hospital", senior.preferredHospital)
                    }
                }
            }
        }

        if (timeline.isNotEmpty()) {
            item {
                Text("Emergency Timeline", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DangerRed, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
            }
            items(timeline) { entry ->
                val indicator = when (entry.severity) { "Critical" -> DangerRed; "Warning" -> WarningAmber; else -> PrimaryTeal }
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                        androidx.compose.foundation.layout.Box(modifier = Modifier.size(12.dp).background(indicator, androidx.compose.foundation.shape.CircleShape))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("${entry.timestamp}  ${entry.title}", fontWeight = FontWeight.Bold, color = indicator)
                            Text(entry.description, fontSize = 14.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Dialog for action confirmations
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
                    Text("OK", color = White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun EmergencyCardRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = PrimaryTeal,
            modifier = Modifier.weight(0.6f)
        )
    }
}
