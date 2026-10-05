package com.careapp.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careapp.app.ui.theme.*
import com.careapp.app.viewmodel.CaregiverNotesViewModel

@Composable
fun CaregiverNotesScreen(viewModel: CaregiverNotesViewModel? = null) {
    val note by (viewModel?.noteContent?.collectAsState() ?: remember { mutableStateOf("") })
    val mood by (viewModel?.mood?.collectAsState() ?: remember { mutableStateOf("Calm") })
    val sleep by (viewModel?.sleepQuality?.collectAsState() ?: remember { mutableStateOf("Good") })
    val summary by (viewModel?.summaryText?.collectAsState() ?: remember { mutableStateOf("") })
    val showDialog by (viewModel?.showDialog?.collectAsState() ?: remember { mutableStateOf(false) })
    val dialogMessage by (viewModel?.dialogMessage?.collectAsState() ?: remember { mutableStateOf("") })
    var breakfast by remember { mutableStateOf(false) }
    var lunch by remember { mutableStateOf(false) }
    var dinner by remember { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Caregiver Handoff", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal) }
        item { Text("Record a few simple observations for the next caregiver.", fontSize = 18.sp) }
        item { Text("Meals", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { MealCheck("Breakfast", breakfast) { breakfast = !breakfast; viewModel?.toggleBreakfast() }; MealCheck("Lunch", lunch) { lunch = !lunch; viewModel?.toggleLunch() }; MealCheck("Dinner", dinner) { dinner = !dinner; viewModel?.toggleDinner() } } }
        item { OutlinedTextField(value = note, onValueChange = { viewModel?.updateNoteContent(it) }, modifier = Modifier.fillMaxWidth(), minLines = 4, label = { Text("Notes") }) }
        item { Text("Mood: $mood", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("Calm", "Happy", "Worried", "Tired").forEach { value -> FilterChip(selected = mood == value, onClick = { viewModel?.setMood(value) }, label = { Text(value) }) } } }
        item { Text("Sleep: $sleep", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("Good", "Okay", "Poor").forEach { value -> FilterChip(selected = sleep == value, onClick = { viewModel?.setSleepQuality(value) }, label = { Text(value) }) } } }
        item { Button(onClick = { viewModel?.saveNote() }, modifier = Modifier.fillMaxWidth()) { Text("Save Handoff Note") } }
        item { OutlinedButton(onClick = { viewModel?.generateHandoffSummary() }, modifier = Modifier.fillMaxWidth()) { Text("Generate Handoff Summary") } }
        if (summary.isNotBlank()) item { Card { Text(summary, modifier = Modifier.padding(16.dp), fontSize = 17.sp) } }
    }
    if (showDialog) AlertDialog(onDismissRequest = { viewModel?.dismissDialog() }, title = { Text("Saved") }, text = { Text(dialogMessage) }, confirmButton = { Button(onClick = { viewModel?.dismissDialog() }) { Text("OK") } })
}

@Composable
private fun MealCheck(label: String, checked: Boolean, onClick: () -> Unit) {
    Column { Checkbox(checked = checked, onCheckedChange = { onClick() }); Text(label, fontSize = 14.sp) }
}
