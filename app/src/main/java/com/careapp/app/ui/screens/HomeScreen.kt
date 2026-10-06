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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.careapp.app.data.SampleData
import com.careapp.app.ui.CareAppScreen
import com.careapp.app.ui.components.LargeButton
import com.careapp.app.ui.components.AdherenceWidget
import com.careapp.app.ui.theme.*
import com.careapp.app.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun HomeScreen(navController: NavController? = null, viewModel: DashboardViewModel? = null) {
    val senior = SampleData.seniorProfile
    val context = androidx.compose.ui.platform.LocalContext.current
    val today = LocalDate.now()
    val dayName = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val monthDay = "${today.monthValue.toString().padStart(2, '0')}/${today.dayOfMonth.toString().padStart(2, '0')}/${today.year}"

    val nextMedication = SampleData.medications.firstOrNull()
    val nextVisitor = "Maya at 5:00 PM"
    val nextTask = "Take morning medicine"
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
                text = "Good morning, ${senior.preferredName}.",
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
                    Text("What was I doing? What do I do next?", color = Color.White, fontSize = 16.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        color = Color.DarkGray
                    )
                    Text(
                        text = "Due at ${nextMedication?.scheduledTime ?: "today"}",
                        fontSize = 16.sp,
                        color = Color.DarkGray,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                OrientationInfoRow("Today is", "$dayName, $monthDay")
                Spacer(modifier = Modifier.height(12.dp))
                OrientationInfoRow("You are at", "home")
                Spacer(modifier = Modifier.height(12.dp))
                OrientationInfoRow("Next task", nextTask)
                Spacer(modifier = Modifier.height(12.dp))
                OrientationInfoRow("Next visitor", nextVisitor)
                Spacer(modifier = Modifier.height(12.dp))
                OrientationInfoRow("Emergency contact", senior.primaryCaregiverName)
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
                onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:${senior.primaryCaregiverPhone}"))) } },
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
            color = Color.DarkGray
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryTeal
        )
    }
}
