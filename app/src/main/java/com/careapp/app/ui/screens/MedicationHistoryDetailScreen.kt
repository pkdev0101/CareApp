package com.careapp.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careapp.app.data.SampleData
import com.careapp.app.ui.theme.*
import com.careapp.app.viewmodel.MedicationHistoryViewModel
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MedicationHistoryScreen(navController: NavController? = null, viewModel: MedicationHistoryViewModel? = null) {
    val grouped by (viewModel?.historyEntries?.collectAsState()
        ?: remember { mutableStateOf(emptyMap<LocalDate, List<MedicationHistoryViewModel.HistoryEntry>>()) })
    val fallback = SampleData.medications.map { medication ->
        MedicationHistoryViewModel.HistoryEntry(LocalDate.now(), medication, medication.scheduledTime, "Pending", null, "")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundLight).padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Text("The last 7 days", fontSize = 18.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp))
        }
        item { OutlinedButton(onClick = { navController?.popBackStack() }, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) { Text("Back to Medicine") } }
        if (grouped.isEmpty()) {
            item { entriesForDate(fallback, LocalDate.now()) }
        } else {
            grouped.forEach { (date, entries) ->
                item {
                    Text(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
                }
                items(entries) { entry -> MedicationHistoryCard(entry) }
            }
        }
    }
}

@Composable
private fun entriesForDate(entries: List<MedicationHistoryViewModel.HistoryEntry>, date: LocalDate) {
    Text(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
    entries.forEach { MedicationHistoryCard(it) }
}

@Composable
fun MedicationHistoryCard(entry: MedicationHistoryViewModel.HistoryEntry) {
    val statusColor = when (entry.status) {
        "Taken" -> SuccessTeal
        "Missed" -> WarningAmber
        "Need Help" -> DangerRed
        else -> Color.Gray
    }
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${entry.medication.name} — ${entry.scheduledTime}", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                Text("${entry.medication.dose} • ${entry.status}", fontSize = 15.sp, color = statusColor, fontWeight = FontWeight.SemiBold)
                entry.completedTime?.let { Text("Actual time: $it", fontSize = 14.sp, color = Color.DarkGray) }
                if (entry.notes.isNotBlank()) Text(entry.notes, fontSize = 13.sp, color = Color.Gray)
            }
            Surface(shape = androidx.compose.foundation.shape.CircleShape, color = statusColor) {
                Text(when (entry.status) { "Taken" -> "✓"; "Missed" -> "!"; "Need Help" -> "?"; else -> "○" }, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
            }
        }
    }
}
