package com.careapp.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.careapp.app.data.*
import com.careapp.app.data.repository.CareRepository
import com.careapp.app.util.CaregiverAuditStore
import com.careapp.app.util.MedicationReminderPreferences
import com.careapp.app.util.NotificationHelper
import kotlinx.coroutines.launch

enum class CareManagementType(val heading: String, val addLabel: String) {
    MEDICATIONS("Manage Medications", "Add Medication"),
    PEOPLE("Manage People", "Add Person"),
    ROUTINES("Manage Routines", "Add Routine"),
    PLACES("Manage Places", "Add Place")
}

@Composable
fun CareManagementScreen(type: CareManagementType, repository: CareRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val audit = remember { CaregiverAuditStore(context) }
    val reminderPreferences = remember { MedicationReminderPreferences(context) }
    val medications by repository.getAllMedications().collectAsState(initial = emptyList())
    val people by repository.getAllMemoryCards().collectAsState(initial = emptyList())
    val places by repository.getAllPlaces().collectAsState(initial = emptyList())
    var routines by remember { mutableStateOf(emptyList<RoutineTask>()) }
    var refresh by remember { mutableIntStateOf(0) }
    var editor by remember { mutableStateOf<Any?>(null) }
    var addNew by remember { mutableStateOf(false) }

    LaunchedEffect(refresh) { routines = repository.getAllRoutineTasksSync() }

    val count = when (type) { CareManagementType.MEDICATIONS -> medications.size; CareManagementType.PEOPLE -> people.size; CareManagementType.ROUTINES -> routines.size; CareManagementType.PLACES -> places.size }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text(type.heading, style = MaterialTheme.typography.headlineSmall)
            Text("$count saved. Select Edit to change details or Delete to remove an entry.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = { addNew = true }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) { Text(type.addLabel) }
        }
        when (type) {
            CareManagementType.MEDICATIONS -> items(medications, key = { "med_${it.id}" }) { medication ->
                ManagementCard("${medication.name} · ${medication.dose}", "${medication.scheduledTime} · ${medication.instructions}", onEdit = { editor = medication }, onDelete = { scope.launch { repository.deleteMedication(medication); reminderPreferences.setEnabled(medication, false); NotificationHelper.cancelMedicationReminder(context, medication); audit.record("Medication deleted: ${medication.name}") } })
            }
            CareManagementType.PEOPLE -> items(people, key = { "person_${it.id}" }) { person ->
                ManagementCard(person.name, "${person.relationship} · ${person.phone}", { editor = person }, { scope.launch { repository.deleteMemoryCard(person); audit.record("Person deleted: ${person.name}") } })
            }
            CareManagementType.ROUTINES -> items(routines, key = { "routine_${it.id}" }) { routine ->
                ManagementCard(routine.title, "${routine.category} · ${routine.suggestedTime}", { editor = routine }, { scope.launch { repository.deleteRoutineTask(routine); refresh++ ; audit.record("Routine deleted: ${routine.title}") } })
            }
            CareManagementType.PLACES -> items(places, key = { "place_${it.id}" }) { place ->
                ManagementCard(place.name, "${place.type} · ${place.notes}", { editor = place }, { scope.launch { repository.deletePlace(place); audit.record("Place deleted: ${place.name}") } })
            }
        }
    }

    if (addNew) {
        when (type) {
            CareManagementType.MEDICATIONS -> MedicationEditor(null, repository, reminderPreferences, context, audit, { addNew = false })
            CareManagementType.PEOPLE -> PersonEditor(null, repository, audit, { addNew = false })
            CareManagementType.ROUTINES -> RoutineEditor(null, repository, audit, { addNew = false; refresh++ })
            CareManagementType.PLACES -> PlaceEditor(null, repository, audit, { addNew = false })
        }
    }
    when (val value = editor) {
        is Medication -> MedicationEditor(value, repository, reminderPreferences, context, audit, { editor = null })
        is MemoryCard -> PersonEditor(value, repository, audit, { editor = null })
        is RoutineTask -> RoutineEditor(value, repository, audit, { editor = null; refresh++ })
        is SavedPlace -> PlaceEditor(value, repository, audit, { editor = null })
    }
}

@Composable private fun ManagementCard(title: String, detail: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(14.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(detail, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { TextButton(onClick = onEdit) { Text("Edit") }; TextButton(onClick = onDelete) { Text("Delete") } }
    } }
}

@Composable private fun EditorDialog(title: String, valid: Boolean, error: String, onDismiss: () -> Unit, onSave: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(title) }, text = { Column(Modifier.heightIn(max = 430.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content) }, confirmButton = { Button(onClick = onSave, enabled = valid) { Text("Save") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable private fun MedicationEditor(existing: Medication?, repository: CareRepository, prefs: MedicationReminderPreferences, context: android.content.Context, audit: CaregiverAuditStore, done: () -> Unit) {
    val scope = rememberCoroutineScope(); var name by remember { mutableStateOf(existing?.name ?: "") }; var dose by remember { mutableStateOf(existing?.dose ?: "") }; var instructions by remember { mutableStateOf(existing?.instructions ?: "") }; var time by remember { mutableStateOf(existing?.scheduledTime ?: "") }; var reminder by remember { mutableStateOf(existing?.let { prefs.isEnabled(it) } ?: false) }; var error by remember { mutableStateOf("") }; var expanded by remember { mutableStateOf(false) }
    EditorDialog(if (existing == null) "Add Medication" else "Edit Medication", name.isNotBlank() && dose.isNotBlank() && instructions.isNotBlank() && time.isNotBlank(), error, done, { if (name.isBlank() || dose.isBlank() || instructions.isBlank() || time.isBlank()) { error = "Name, dose, instructions, and time are required." } else scope.launch { val med = Medication(existing?.id ?: 0, name.trim(), dose.trim(), time, instructions.trim(), existing?.pillDescription ?: "", existing?.reason ?: "", existing?.doctor ?: "", existing?.caregiverConfirmationRequired ?: false); val saved = if (existing == null) repository.insertMedication(med) else { repository.updateMedication(med); med }; prefs.setEnabled(saved, reminder); if (reminder) NotificationHelper.scheduleMedicationReminder(context, saved) else NotificationHelper.cancelMedicationReminder(context, saved); audit.record("Medication ${if (existing == null) "added" else "updated"}: ${saved.name}"); done() } }) {
        OutlinedTextField(name, { name = it }, label = { Text("Medication name *") }, singleLine = true)
        OutlinedTextField(dose, { dose = it }, label = { Text("Dose *") }, singleLine = true)
        OutlinedTextField(instructions, { instructions = it }, label = { Text("Instructions *") })
        Box { OutlinedTextField(time, {}, readOnly = true, label = { Text("Time *") }, placeholder = { Text("Choose a 15-minute time slot") }, modifier = Modifier.fillMaxWidth().clickable { expanded = true }); DropdownMenu(expanded, { expanded = false }, modifier = Modifier.heightIn(max = 300.dp)) { medicationTimeSlots().forEach { DropdownMenuItem(text = { Text(it) }, onClick = { time = it; expanded = false }) } } }
        Row { Checkbox(reminder, { reminder = it }); Text("Require notification reminder", modifier = Modifier.padding(top = 12.dp)) }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
    }
}

@Composable private fun PersonEditor(existing: MemoryCard?, repository: CareRepository, audit: CaregiverAuditStore, done: () -> Unit) { val scope = rememberCoroutineScope(); var name by remember { mutableStateOf(existing?.name ?: "") }; var relationship by remember { mutableStateOf(existing?.relationship ?: "") }; var phone by remember { mutableStateOf(existing?.phone ?: "") }; var notes by remember { mutableStateOf(existing?.memoryCue ?: "") }; var error by remember { mutableStateOf("") }; EditorDialog(if (existing == null) "Add Person" else "Edit Person", name.isNotBlank() && relationship.isNotBlank() && phone.isNotBlank(), error, done, { if (name.isBlank() || relationship.isBlank() || phone.isBlank()) error = "Name, relationship, and phone are required." else scope.launch { val saved = MemoryCard(existing?.id ?: 0, name.trim(), relationship.trim(), notes.trim(), existing?.helpsWith ?: "", phone.trim(), existing?.photoUri); if (existing == null) repository.insertMemoryCard(saved) else repository.updateMemoryCard(saved); audit.record("Person ${if (existing == null) "added" else "updated"}: ${saved.name}"); done() } }) { OutlinedTextField(name, { name = it }, label = { Text("Name *") }, singleLine = true); OutlinedTextField(relationship, { relationship = it }, label = { Text("Relationship *") }, singleLine = true); OutlinedTextField(phone, { phone = it }, label = { Text("Phone *") }, singleLine = true); OutlinedTextField(notes, { notes = it }, label = { Text("Memory cue") }) } }

@Composable private fun RoutineEditor(existing: RoutineTask?, repository: CareRepository, audit: CaregiverAuditStore, done: () -> Unit) { val scope = rememberCoroutineScope(); var title by remember { mutableStateOf(existing?.title ?: "") }; var category by remember { mutableStateOf(existing?.category ?: "Morning") }; var time by remember { mutableStateOf(existing?.suggestedTime ?: "") }; var error by remember { mutableStateOf("") }; EditorDialog(if (existing == null) "Add Routine" else "Edit Routine", title.isNotBlank() && category.isNotBlank() && time.isNotBlank(), error, done, { if (title.isBlank() || category.isBlank() || time.isBlank()) error = "Routine name, part of day, and time are required." else scope.launch { val saved = RoutineTask(existing?.id ?: 0, title.trim(), category.trim(), time.trim(), existing?.completed ?: false, existing?.completedTime, existing?.notes ?: ""); if (existing == null) repository.insertRoutineTask(saved) else repository.updateRoutineTask(saved); audit.record("Routine ${if (existing == null) "added" else "updated"}: ${saved.title}"); done() } }) { OutlinedTextField(title, { title = it }, label = { Text("Routine name *") }, singleLine = true); OutlinedTextField(category, { category = it }, label = { Text("Morning / Afternoon / Evening *") }, singleLine = true); OutlinedTextField(time, { time = it }, label = { Text("Suggested time *") }, singleLine = true) } }

@Composable private fun PlaceEditor(existing: SavedPlace?, repository: CareRepository, audit: CaregiverAuditStore, done: () -> Unit) { val scope = rememberCoroutineScope(); var name by remember { mutableStateOf(existing?.name ?: "") }; var type by remember { mutableStateOf(existing?.type ?: "Safe place") }; var notes by remember { mutableStateOf(existing?.notes ?: "") }; var error by remember { mutableStateOf("") }; EditorDialog(if (existing == null) "Add Place" else "Edit Place", name.isNotBlank() && type.isNotBlank(), error, done, { if (name.isBlank() || type.isBlank()) error = "Place name and type are required." else scope.launch { val saved = SavedPlace(existing?.id ?: 0, name.trim(), type.trim(), existing?.latitude ?: 0.0, existing?.longitude ?: 0.0, notes.trim()); if (existing == null) repository.insertPlace(saved) else repository.updatePlace(saved); audit.record("Place ${if (existing == null) "added" else "updated"}: ${saved.name}"); done() } }) { OutlinedTextField(name, { name = it }, label = { Text("Place name *") }, singleLine = true); OutlinedTextField(type, { type = it }, label = { Text("Place type *") }, singleLine = true); OutlinedTextField(notes, { notes = it }, label = { Text("Address or notes") }) } }

private fun medicationTimeSlots(): List<String> = buildList { for (hour in 0..23) for (minute in 0 until 60 step 15) { val suffix = if (hour < 12) "AM" else "PM"; val displayHour = if (hour % 12 == 0) 12 else hour % 12; add("%d:%02d %s".format(displayHour, minute, suffix)) } }
