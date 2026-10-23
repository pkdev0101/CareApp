package com.careapp.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careapp.app.data.MemoryCard
import com.careapp.app.data.Medication
import com.careapp.app.data.RoutineTask
import com.careapp.app.data.repository.CareRepository
import com.careapp.app.util.EmergencyProfileData
import com.careapp.app.util.EmergencyProfileStore
import com.careapp.app.util.OnboardingStore
import com.careapp.app.util.PatientProfileData
import com.careapp.app.util.PatientProfileStore
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    repository: CareRepository,
    onboardingStore: OnboardingStore,
    initialStep: Int,
    onFinished: () -> Unit,
    onSaveAndExit: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { onboardingStore.markStarted() }
    var step by remember { mutableIntStateOf(initialStep.coerceIn(0, 5)) }
    var validationMessage by remember { mutableStateOf("") }

    var preferredName by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var memoryNote by remember { mutableStateOf("") }
    var communicationStyle by remember { mutableStateOf("") }
    var contactName by remember { mutableStateOf("") }
    var contactRelationship by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var medicationName by remember { mutableStateOf("") }
    var medicationDose by remember { mutableStateOf("") }
    var medicationTime by remember { mutableStateOf("") }
    var medicationPeriod by remember { mutableStateOf("Morning") }
    var routineName by remember { mutableStateOf("") }
    var routineStep by remember { mutableStateOf("") }
    var routineTime by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var conditions by remember { mutableStateOf("") }
    var emergencyMessage by remember { mutableStateOf("") }
    var calmingInstructions by remember { mutableStateOf("") }
    var counts by remember { mutableStateOf(emptyMap<String, Int>()) }

    LaunchedEffect(Unit) {
        val profile = PatientProfileStore(context).load()
        preferredName = profile.name
        fullName = profile.fullName
        memoryNote = profile.memoryNote
        communicationStyle = profile.communicationStyle
        val emergency = EmergencyProfileStore(context).load()
        emergencyContact = emergency.emergencyContact
        allergies = emergency.allergies
        conditions = emergency.conditions
        emergencyMessage = emergency.memoryNote
        calmingInstructions = emergency.calmingInstructions
    }

    LaunchedEffect(step) {
        onboardingStore.saveStep(step)
        if (step == 5) {
            val profile = PatientProfileStore(context).load()
            val emergency = EmergencyProfileStore(context)
            counts = mapOf(
                "profile" to if (profile.name.isNotBlank() || profile.fullName.isNotBlank()) 1 else 0,
                "contacts" to repository.getAllMemoryCardsSync().size,
                "medications" to repository.getAllMedicationsSync().size,
                "routines" to repository.getAllRoutineTasksSync().size,
                "emergency" to if (emergency.hasProfile()) 1 else 0,
                "places" to repository.getAllPlacesSync().size
            )
        }
    }

    fun moveNext() {
        validationMessage = ""
        when (step) {
            0 -> {
                if (preferredName.isBlank() || fullName.isBlank()) {
                    validationMessage = "Enter a preferred name and full name to continue."
                    return
                }
                PatientProfileStore(context).save(
                    PatientProfileData(
                        name = preferredName.trim(),
                        fullName = fullName.trim(),
                        age = "",
                        dateOfBirth = "",
                        relationship = relationship.trim(),
                        memoryNote = memoryNote.trim(),
                        communicationStyle = communicationStyle.trim()
                    )
                )
            }
            1 -> if (contactName.isNotBlank() && contactPhone.isBlank()) {
                validationMessage = "Add a phone number or leave the contact blank."
                return
            } else if (contactName.isNotBlank()) {
                scope.launch {
                    repository.insertMemoryCard(
                        MemoryCard(
                            name = contactName.trim(),
                            relationship = contactRelationship.ifBlank { "Caregiver" }.trim(),
                            memoryCue = "Trusted caregiver",
                            helpsWith = "Care and daily support",
                            phone = contactPhone.trim()
                        )
                    )
                    contactName = ""
                    contactRelationship = ""
                    contactPhone = ""
                    step++
                }
                return
            }
            2 -> if (medicationName.isNotBlank()) {
                if (medicationDose.isBlank() || medicationTime.isBlank()) {
                    validationMessage = "Enter the dose/instructions and scheduled time, exactly as provided by the caregiver or clinician."
                    return
                }
                scope.launch {
                    repository.insertMedication(
                        Medication(
                            name = medicationName.trim(),
                            dose = medicationDose.trim(),
                            scheduledTime = medicationTime.trim(),
                            instructions = medicationDose.trim(),
                            pillDescription = "",
                            reason = "",
                            doctor = ""
                        )
                    )
                    medicationName = ""
                    medicationDose = ""
                    medicationTime = ""
                    step++
                }
                return
            }
            3 -> if (routineName.isNotBlank() || routineStep.isNotBlank()) {
                if (routineName.isBlank() || routineStep.isBlank()) {
                    validationMessage = "Enter both a routine name and its next step."
                    return
                }
                scope.launch {
                    repository.insertRoutineTask(RoutineTask(title = routineStep.trim(), category = routineName.trim(), suggestedTime = routineTime.trim().ifBlank { "Not scheduled" }))
                    routineName = ""
                    routineStep = ""
                    routineTime = ""
                    step++
                }
                return
            }
            4 -> {
                EmergencyProfileStore(context).save(
                    EmergencyProfileData(
                        memoryNote = emergencyMessage.trim(),
                        allergies = allergies.trim(),
                        conditions = conditions.trim(),
                        emergencyContact = emergencyContact.trim(),
                        preferredHospital = "",
                        calmingInstructions = calmingInstructions.trim()
                    )
                )
            }
        }
        if (step < 5) step++ else {
            onboardingStore.markCompleted()
            onFinished()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { if (step > 0) step-- }) { Text("Back") }
            Text("Step ${step + 1} of 6", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 12.dp))
            TextButton(onClick = { onboardingStore.saveStep(step); onSaveAndExit() }) { Text("Save and Exit") }
        }
        LinearProgressIndicator(progress = { (step + 1) / 6f }, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            when (step) {
                0 -> StepProfile(preferredName, { preferredName = it }, fullName, { fullName = it }, relationship, { relationship = it }, memoryNote, { memoryNote = it }, communicationStyle, { communicationStyle = it })
                1 -> StepContact(contactName, { contactName = it }, contactRelationship, { contactRelationship = it }, contactPhone, { contactPhone = it })
                2 -> StepMedication(medicationName, { medicationName = it }, medicationDose, { medicationDose = it }, medicationTime, { medicationTime = it }, medicationPeriod, { medicationPeriod = it })
                3 -> StepRoutine(routineName, { routineName = it }, routineStep, { routineStep = it }, routineTime, { routineTime = it })
                4 -> StepEmergency(emergencyContact, { emergencyContact = it }, allergies, { allergies = it }, conditions, { conditions = it }, emergencyMessage, { emergencyMessage = it }, calmingInstructions, { calmingInstructions = it })
                5 -> StepReview(counts)
            }
            if (validationMessage.isNotBlank()) Text(validationMessage, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }
        Button(onClick = { moveNext() }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text(if (step == 5) "Finish Setup" else "Continue") }
        if (step in 1..4) TextButton(onClick = { validationMessage = ""; step++ }, modifier = Modifier.fillMaxWidth()) { Text("Skip for Now") }
    }
    }
}

@Composable private fun StepProfile(preferred: String, onPreferred: (String) -> Unit, full: String, onFull: (String) -> Unit, relationship: String, onRelationship: (String) -> Unit, note: String, onNote: (String) -> Unit, style: String, onStyle: (String) -> Unit) {
    StepHeading("Senior Profile", "This helps CareApp personalize orientation and emergency assistance. A diagnosis is not required.")
    Field(preferred, onPreferred, "Preferred name *")
    Field(full, onFull, "Full name *")
    Field(relationship, onRelationship, "Caregiver relationship (optional)")
    Field(note, onNote, "Memory-loss or communication note", "I may have trouble remembering recent events.")
    Field(style, onStyle, "Preferred communication style", "Please speak slowly and use simple sentences.")
}

@Composable private fun StepContact(name: String, onName: (String) -> Unit, relationship: String, onRelationship: (String) -> Unit, phone: String, onPhone: (String) -> Unit) {
    StepHeading("Trusted Contacts", "Add one trusted caregiver. You can add more later in Caregiver Mode.")
    Field(name, onName, "Contact name")
    Field(relationship, onRelationship, "Relationship", "Daughter")
    Field(phone, onPhone, "Phone number")
    Text("Manual entry is supported. No contacts database access is required.", style = MaterialTheme.typography.bodyMedium)
}

@Composable private fun StepMedication(name: String, onName: (String) -> Unit, dose: String, onDose: (String) -> Unit, time: String, onTime: (String) -> Unit, period: String, onPeriod: (String) -> Unit) {
    StepHeading("Medication Setup", "Enter the prescribed information exactly as provided. CareApp never recommends a dose.")
    Field(name, onName, "Medication name")
    Field(dose, onDose, "Instructions or prescribed dose")
    Field(time, onTime, "Scheduled time", "8:00 AM")
    Field(period, onPeriod, "Part of day", "Morning / Afternoon / Evening / Bedtime")
    Text("A reminder does not mean a medication was taken. A senior or caregiver must explicitly record it.", style = MaterialTheme.typography.bodyMedium)
}

@Composable private fun StepRoutine(name: String, onName: (String) -> Unit, step: String, onStep: (String) -> Unit, time: String, onTime: (String) -> Unit) {
    StepHeading("Daily Routines and My Day", "Add a simple routine and its next step. More steps can be added later in Caregiver Mode.")
    Field(name, onName, "Routine name", "Getting Ready")
    Field(step, onStep, "First or next step", "Brush teeth")
    Field(time, onTime, "Optional scheduled time", "8:00 AM")
}

@Composable private fun StepEmergency(contact: String, onContact: (String) -> Unit, allergies: String, onAllergies: (String) -> Unit, conditions: String, onConditions: (String) -> Unit, message: String, onMessage: (String) -> Unit, calming: String, onCalming: (String) -> Unit) {
    StepHeading("Emergency and Safety", "Basic emergency information is kept separate from sensitive details and can be skipped.")
    Field(contact, onContact, "Primary emergency contact")
    Field(allergies, onAllergies, "Important allergies (optional)")
    Field(conditions, onConditions, "Medical or communication notes (optional)")
    Field(message, onMessage, "Emergency helper message", "I may have memory loss. Please speak slowly and contact my caregiver.")
    Field(calming, onCalming, "What helps when confused", "Please stay calm and show me my care card.")
}

@Composable private fun StepReview(counts: Map<String, Int>) {
    StepHeading("CareApp Is Almost Ready", "Review what has been saved. Optional sections can remain incomplete.")
    val labels = listOf("Senior Profile" to "profile", "Trusted Contact" to "contacts", "Medications" to "medications", "Routines" to "routines", "Emergency Information" to "emergency", "Saved Places" to "places")
    labels.forEach { (label, key) ->
        val count = counts[key] ?: 0
        ListItem(headlineContent = { Text(label) }, supportingContent = { Text(if (count > 0) "Complete" else if (key == "places") "Optional — Not Added" else "Not configured") }, trailingContent = { Text(if (count > 0) "✓" else "—", color = MaterialTheme.colorScheme.primary, fontSize = 22.sp) })
    }
}

@Composable private fun StepHeading(title: String, description: String) { Text(title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary); Text(description, style = MaterialTheme.typography.bodyLarge) }

@Composable
private fun Field(value: String, onValue: (String) -> Unit, label: String, placeholder: String = "") {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        placeholder = { if (placeholder.isNotBlank()) Text(placeholder) },
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        singleLine = false,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            errorCursorColor = MaterialTheme.colorScheme.error
        )
    )
}

