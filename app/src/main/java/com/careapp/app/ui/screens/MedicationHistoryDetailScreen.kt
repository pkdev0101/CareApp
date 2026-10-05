package com.careapp.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careapp.app.data.SampleData
import com.careapp.app.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MedicationHistoryScreen(viewModel: com.careapp.app.viewmodel.MedicationViewModel? = null) {
    val medications by (viewModel?.medications?.collectAsState() ?: remember { mutableStateOf(SampleData.medications) })
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Text(
                text = "Medication History",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        item {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items(medications) { medication ->
            MedicationHistoryCard(medication)
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun MedicationHistoryCard(medication: com.careapp.app.data.Medication) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${medication.scheduledTime} — ${medication.name}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${medication.dose} — ${medication.instructions}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        color = SuccessTeal,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
