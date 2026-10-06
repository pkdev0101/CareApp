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
import androidx.navigation.NavController
import com.careapp.app.ui.CareAppScreen
import com.careapp.app.ui.components.CareSectionCard

@Composable
fun RoutineScreen(viewModel: RoutineViewModel? = null, isCaregiverMode: Boolean = false, navController: NavController? = null) {
    val morning by (viewModel?.morningTasks?.collectAsState() ?: remember { mutableStateOf(SampleData.routineTasks.filter { it.category == "Morning" }) })
    val afternoon by (viewModel?.afternoonTasks?.collectAsState() ?: remember { mutableStateOf(SampleData.routineTasks.filter { it.category == "Afternoon" }) })
    val evening by (viewModel?.eveningTasks?.collectAsState() ?: remember { mutableStateOf(SampleData.routineTasks.filter { it.category == "Evening" }) })
    val showDialog by (viewModel?.showDialog?.collectAsState() ?: remember { mutableStateOf(false) })
    val dialogTitle by (viewModel?.dialogTitle?.collectAsState() ?: remember { mutableStateOf("") })
    val dialogMessage by (viewModel?.dialogMessage?.collectAsState() ?: remember { mutableStateOf("") })
    LazyColumn(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp), contentPadding = PaddingValues(bottom = 96.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("My Routine", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(vertical = 12.dp)) }
        item { Text(if (isCaregiverMode) "Review the routine, or open Care Setup to edit it." else "Tap a task when it is complete.", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp)) }
        if (isCaregiverMode) item { Button(onClick = { navController?.navigate(CareAppScreen.CareSetup.name) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Edit routines in Care Setup") } }
        item { RoutineSection("Morning", morning, viewModel) }
        item { RoutineSection("Afternoon", afternoon, viewModel) }
        item { RoutineSection("Evening", evening, viewModel) }
    }
    if (showDialog) AlertDialog(onDismissRequest = { viewModel?.dismissDialog() }, title = { Text(dialogTitle, fontWeight = FontWeight.Bold) }, text = { Text(dialogMessage, fontSize = 16.sp) }, confirmButton = { Button(onClick = { viewModel?.dismissDialog() }) { Text("OK") } })
}

@Composable
private fun RoutineSection(category: String, tasks: List<RoutineTask>, viewModel: RoutineViewModel?) {
    CareSectionCard(category) {
    tasks.forEach { task ->
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(task.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Suggested: ${task.suggestedTime}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (task.completed) Text("✓ Completed at ${task.completedTime}", fontSize = 12.sp, color = SuccessTeal, fontWeight = FontWeight.Bold)
                }
                Checkbox(checked = task.completed, onCheckedChange = { viewModel?.toggleTaskCompletion(task) })
            }
        }
    }
    }
}
