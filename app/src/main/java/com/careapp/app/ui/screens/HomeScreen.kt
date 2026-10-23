package com.careapp.app.ui.screens

import android.content.Intent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.careapp.app.ui.CareAppScreen
import com.careapp.app.ui.components.LargeButton
import com.careapp.app.ui.components.AdherenceWidget
import com.careapp.app.ui.theme.*
import com.careapp.app.viewmodel.DashboardViewModel
import com.careapp.app.viewmodel.MemoryCardViewModel
import com.careapp.app.util.PatientProfileStore
import com.careapp.app.util.DemoModeController
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.*

@Composable
fun HomeScreen(navController: NavController? = null, viewModel: DashboardViewModel? = null, memoryViewModel: MemoryCardViewModel? = null) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var patientProfile by remember { mutableStateOf(PatientProfileStore(context).load()) }
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                patientProfile = PatientProfileStore(context).load()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val today = LocalDate.now()
    val greeting = when (LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        in 18..21 -> "Good evening"
        else -> "Good night"
    }
    val dayName = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val monthDay = "${today.monthValue.toString().padStart(2, '0')}/${today.dayOfMonth.toString().padStart(2, '0')}/${today.year}"

    val nextMedication by (viewModel?.nextMedication?.collectAsState() ?: remember { mutableStateOf(null) })
    val nextRoutineTask by (viewModel?.nextRoutineTask?.collectAsState() ?: remember { mutableStateOf(null) })
    val memoryCards by (memoryViewModel?.memoryCards?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    val nextVisitor = if (DemoModeController.isActive(context)) {
        "Sarah Miller at 5:00 PM"
    } else {
        "Ask your caregiver to add a visit"
    }
    val nextTask = nextRoutineTask?.title ?: nextMedication?.let { "Take ${it.name}" } ?: "Ask your caregiver to add a next step"
    val medicationCount by (viewModel?.medicationCount?.collectAsState() ?: remember { mutableStateOf(3) })
    val medicationsTaken by (viewModel?.medicationsTaken?.collectAsState() ?: remember { mutableStateOf(0) })
    val adherence by (viewModel?.adherencePercentage?.collectAsState() ?: remember { mutableStateOf(0) })

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Welcome
        item {
            Text(
                text = "$greeting, ${patientProfile.name.ifBlank { "there" }}.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = SupportPlum), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    LargeButton(text = "Help Me Remember", onClick = { navController?.navigate(CareAppScreen.Help.name) }, backgroundColor = SupportPlum, modifier = Modifier.padding(0.dp))
                    Text("What was I doing? What do I do next?", color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Text(
                        text = "Next Step",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = nextTask,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = nextRoutineTask?.let { "Current routine · ${it.category}" }
                            ?: "Next medication · ${nextMedication?.scheduledTime ?: "today"}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    OutlinedButton(
                        onClick = { navController?.navigate(CareAppScreen.Medication.name) },
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                    ) {
                        Text("Open Medicine", fontSize = 17.sp)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Orientation Information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    OrientationInfoRow("Today is", "$dayName, $monthDay")
                    Spacer(modifier = Modifier.height(12.dp))
                    OrientationInfoRow("You are at", "home")
                    Spacer(modifier = Modifier.height(12.dp))
                    OrientationInfoRow("Next task", nextTask)
                    Spacer(modifier = Modifier.height(12.dp))
                    OrientationInfoRow("Next visitor", nextVisitor)
                    Spacer(modifier = Modifier.height(12.dp))
                    OrientationInfoRow("Emergency contact", memoryCards.firstOrNull()?.name ?: "Not configured")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Medication Adherence Widget
        item {
            AdherenceWidget(percentage = adherence, medicationsTaken = medicationsTaken, medicationsTotal = medicationCount)
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Main navigation buttons
        item {
            Text(
                text = "What do you need?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        item {
            LargeButton(
                text = "My Medicine",
                onClick = { navController?.navigate(CareAppScreen.Medication.name) },
                backgroundColor = PrimaryTealLight
            )
        }

        item {
            LargeButton(
                text = "People",
                onClick = { navController?.navigate(CareAppScreen.Family.name) },
                backgroundColor = SecondaryCoral
            )
        }

        item {
            LargeButton(
                text = "Call Caregiver",
                onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:${memoryCards.firstOrNull()?.phone.orEmpty()}"))) } },
                backgroundColor = SupportPlumLight
            )
        }

        item {
            LargeButton(
                text = "Emergency Help",
                onClick = { navController?.navigate(CareAppScreen.Help.name) },
                backgroundColor = DangerRed
            )
        }

        item {
            LargeButton(
                text = "I May Be Lost",
                onClick = { navController?.navigate(CareAppScreen.Lost.name) },
                backgroundColor = SupportPlumLight
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun OrientationInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryTeal
        )
    }
}
