package com.careapp.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
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
import com.careapp.app.ui.CareAnchorScreen
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
            .background(BackgroundLight)
            .padding(16.dp),
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
                onClick = { navController?.navigate(CareAnchorScreen.Medication.name) },
                backgroundColor = PrimaryTealLight
            )
        }

        item {
            LargeButton(
                text = "My Family",
                onClick = { navController?.navigate(CareAnchorScreen.Family.name) },
                backgroundColor = SecondaryCoral
            )
        }

        item {
            LargeButton(
                text = "I Feel Confused",
                onClick = { navController?.navigate(CareAnchorScreen.CalmMode.name) },
                backgroundColor = SupportPlum
            )
        }

        item {
            LargeButton(
                text = "Emergency Help",
                onClick = { navController?.navigate(CareAnchorScreen.Emergency.name) },
                backgroundColor = DangerRed
            )
        }

        item {
            LargeButton(
                text = "Where Am I?",
                onClick = { navController?.navigate(CareAnchorScreen.Lost.name) },
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
