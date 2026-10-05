package com.careapp.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careapp.app.data.RoutineTask
import com.careapp.app.data.SampleData
import com.careapp.app.ui.theme.*
import com.careapp.app.viewmodel.RoutineViewModel

@Composable
fun RoutineScreen(viewModel: RoutineViewModel? = null) {
    val morning by (viewModel?.morningTasks?.collectAsState() ?: remember { mutableStateOf(SampleData.routineTasks.filter { it.category == "Morning" }) })
    val afternoon by (viewModel?.afternoonTasks?.collectAsState() ?: remember { mutableStateOf(SampleData.routineTasks.filter { it.category == "Afternoon" }) })
    val evening by (viewModel?.eveningTasks?.collectAsState() ?: remember { mutableStateOf(SampleData.routineTasks.filter { it.category == "Evening" }) })
    val showDialog by (viewModel?.showDialog?.collectAsState() ?: remember { mutableStateOf(false) })
    val dialogTitle by (viewModel?.dialogTitle?.collectAsState() ?: remember { mutableStateOf("") })
    val dialogMessage by (viewModel?.dialogMessage?.collectAsState() ?: remember { mutableStateOf("") })
    LazyColumn(Modifier.fillMaxSize().background(BackgroundLight).padding(16.dp)) {
        item { Text("My Routine", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal, modifier = Modifier.padding(vertical = 16.dp)) }
        item { Text("Tap a task when it is complete.", fontSize = 18.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp)) }
        item { RoutineSection("Morning", morning, viewModel) }
        item { RoutineSection("Afternoon", afternoon, viewModel) }
        item { RoutineSection("Evening", evening, viewModel) }
    }
    if (showDialog) AlertDialog(onDismissRequest = { viewModel?.dismissDialog() }, title = { Text(dialogTitle, fontWeight = FontWeight.Bold) }, text = { Text(dialogMessage, fontSize = 16.sp) }, confirmButton = { Button(onClick = { viewModel?.dismissDialog() }) { Text("OK") } })
}

@Composable
private fun RoutineSection(category: String, tasks: List<RoutineTask>, viewModel: RoutineViewModel?) {
    Text(category, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal, modifier = Modifier.padding(top = 12.dp))
    tasks.forEach { task ->
        Card(Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(task.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    Text("Suggested: ${task.suggestedTime}", fontSize = 14.sp, color = Color.Gray)
                    if (task.completed) Text("✓ Completed at ${task.completedTime}", fontSize = 12.sp, color = SuccessTeal, fontWeight = FontWeight.Bold)
                }
                Checkbox(checked = task.completed, onCheckedChange = { viewModel?.toggleTaskCompletion(task) })
            }
        }
    }
}
