package com.careapp.app.ui.screens

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.careapp.app.data.Medication
import com.careapp.app.data.RoutineTask
import com.careapp.app.data.MemoryCard
import com.careapp.app.data.repository.CareRepository
import com.careapp.app.ui.CareAppScreen
import com.careapp.app.ui.components.CareSectionCard
import com.careapp.app.viewmodel.MemoryCardViewModel
import com.careapp.app.ui.components.LargeButton
import com.careapp.app.ui.theme.*
import com.careapp.app.util.PatientProfileStore
import com.careapp.app.util.ActiveRoutineStore
import com.careapp.app.util.ContextEngine
import java.time.LocalDate

@Composable
fun CalmModeScreen(navController: NavController? = null, viewModel: MemoryCardViewModel? = null) {
    val context = LocalContext.current
    var showBreathingTimer by remember { mutableStateOf(false) }
    var breathPhase by remember { mutableStateOf("inhale") }
    val repository = remember { CareRepository.getInstance(context) }
    var medications by remember { mutableStateOf(emptyList<Medication>()) }
    var routineTasks by remember { mutableStateOf(emptyList<RoutineTask>()) }
    var medicationLogs by remember { mutableStateOf(emptyList<com.careapp.app.data.MedicationLog>()) }
    val memoryCards by (viewModel?.memoryCards?.collectAsState() ?: remember { mutableStateOf(emptyList<MemoryCard>()) })
    val profile = remember { PatientProfileStore(context).load() }
    LaunchedEffect(Unit) {
        medications = repository.getAllMedicationsSync()
        routineTasks = repository.getAllRoutineTasksSync()
        medicationLogs = repository.getMedicationLogsForDate(LocalDate.now().toString())
    }
    val nextMedication = medications.firstOrNull()
    val activeCategory = remember { ActiveRoutineStore(context).activeCategory() }
    val nextRoutine = ContextEngine.nextRoutineTask(routineTasks, activeCategory)
    val routineProgressSummary = ContextEngine.routineProgressSummary(routineTasks, activeCategory)
    val nextTask = nextRoutine?.title ?: nextMedication?.let { "take ${it.name}" } ?: "ask your caregiver to add a next step"
    val nextMedicationAnswer = ContextEngine.medicationAnswer(medications, medicationLogs)

    val scale = remember { Animatable(1f) }
    LaunchedEffect(showBreathingTimer) {
        if (showBreathingTimer) {
            while (true) {
                // Inhale: scale up for 4 seconds
                scale.animateTo(1.3f, animationSpec = tween(4000, easing = LinearEasing))
                breathPhase = "hold"
                // Hold: 4 seconds
                delay(4000)
                // Exhale: scale down for 4 seconds
                scale.animateTo(1f, animationSpec = tween(4000, easing = LinearEasing))
                breathPhase = "exhale"
                delay(2000)
                breathPhase = "inhale"
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "I forgot…",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "That’s okay. CareApp can help you remember what is happening.",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        item {
            Text(
                text = "Context Recovery",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            ContextRecoveryCard(
                title = "What was I doing?",
                answer = routineProgressSummary,
                highlightedNextStep = nextRoutine?.title
            )
        }

        item {
            ContextRecoveryCard(
                title = "What do I do next?",
                answer = if (nextRoutine != null) "Next: ${nextRoutine.title}. Take your time and ask your caregiver if you are unsure." else "There is no active routine step yet. Start a routine before asking what to do next.",
                actionLabel = "Open Medicine",
                onAction = { navController?.navigate(CareAppScreen.Medication.name) }
            )
        }

        item {
            ContextRecoveryCard(
                title = "Did I take my medicine?",
                answer = nextMedicationAnswer,
                actionLabel = "Open Medicine",
                onAction = { navController?.navigate(CareAppScreen.Medication.name) }
            )
        }

        item {
            ContextRecoveryCard(
                title = "Where am I?",
                answer = "Your caregiver has not added a current location here. Open Help for location and safety options.",
                actionLabel = "Open Help",
                onAction = { navController?.navigate(CareAppScreen.Help.name) }
            )
        }

        item {
            ContextRecoveryCard(
                title = "Who can I call?",
                answer = ContextEngine.contactAnswer(memoryCards),
                actionLabel = "Call Caregiver",
                onAction = {
                    runCatching {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = android.net.Uri.parse("tel:${memoryCards.firstOrNull()?.phone.orEmpty()}")
                        }
                        context.startActivity(intent)
                    }
                }
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Next Step", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(nextTask, fontSize = 21.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Due at ${nextMedication?.scheduledTime ?: "today"}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                    Button(
                        onClick = { navController?.navigate(CareAppScreen.Medication.name) },
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTealLight)
                    ) {
                        Text("Open Medicine", fontSize = 17.sp, color = Color.White)
                    }
                }
            }
        }

        item {
            Text(
                text = "Need to calm down?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("You are safe.", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Take your time. ${profile.memoryNote.ifBlank { "Your caregiver can add calming instructions in Care Setup." }}", fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        // Breathing Timer Button
        item {
            if (!showBreathingTimer) {
                LargeButton(
                    text = "Start Calming Breath",
                    onClick = { showBreathingTimer = true },
                    backgroundColor = SupportPlumLight
                )
            }
        }

        // Breathing Timer Display
        if (showBreathingTimer) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .scale(scale.value)
                            .background(
                                color = SupportPlum.copy(alpha = 0.6f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (breathPhase) {
                                "inhale" -> "Breathe In"
                                "hold" -> "Hold"
                                else -> "Breathe Out"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { showBreathingTimer = false },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                    ) {
                        Text("Stop Breathing Timer")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Repeated-question helper
        item {
            CareSectionCard("Common Questions") {
                Text("Tap a question to see a calm, simple answer.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            CommonQuestionCard(
                "Where am I?",
                "Your caregiver has not added a home address here. Open Lost Mode for available location and safety information."
            )
        }

        item {
            CommonQuestionCard("Did I take my medicine?", "Check the Medicine screen for today's medication status. If you are unsure, please call your caregiver.")
        }

        item {
            CommonQuestionCard("Who is visiting today?", "Your caregiver has not added a visit yet. Take your time and ask Caregiver Mode for help.")
        }

        item {
            CommonQuestionCard("What should I do next?", if (nextRoutine != null) "Your next routine step is ${nextRoutine.title}." else "There is no active routine step saved yet.")
        }

        item {
            CommonQuestionCard("Who can I call?", if (memoryCards.isNotEmpty()) "You can call ${memoryCards.first().name} at ${memoryCards.first().phone}." else "Your caregiver has not added a contact yet.")
        }

        item { WhatHelpsTracker() }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Action Buttons
        item {
            Text(
                text = "What do you need?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        item {
            LargeButton(
                text = "Call My Caregiver",
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = android.net.Uri.parse("tel:${memoryCards.firstOrNull()?.phone.orEmpty()}")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {}
                },
                backgroundColor = PrimaryTealLight
            )
        }

        item {
            LargeButton(
                text = "Show People Photos",
                onClick = { navController?.navigate(CareAppScreen.Family.name) },
                backgroundColor = SecondaryCoral
            )
        }

        item {
            LargeButton(
                text = "Emergency Help",
                onClick = { navController?.navigate(CareAppScreen.Emergency.name) },
                backgroundColor = DangerRed
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun ContextRecoveryCard(
    title: String,
    answer: String,
    highlightedNextStep: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var highlightNextStep by remember { mutableStateOf(false) }
    LaunchedEffect(expanded) {
        if (expanded && highlightedNextStep != null) {
            highlightNextStep = true
            delay(1800)
            highlightNextStep = false
        } else {
            highlightNextStep = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            if (expanded) {
                Text(answer, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
                highlightedNextStep?.let { step ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            if (highlightNextStep) 3.dp else 1.dp,
                            if (highlightNextStep) SupportPlum else PrimaryTeal.copy(alpha = 0.45f)
                        ),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(12.dp)) {
                            Text("Next step", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                            Text(step, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
                actionLabel?.let { label ->
                    Button(
                        onClick = { onAction?.invoke() },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(top = 10.dp),
                    ) {
                        Text(label, fontSize = 16.sp)
                    }
                }
            } else {
                Text("Tap to see the answer", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun WhatHelpsTracker() {
    val counts = remember { mutableStateMapOf<String, Int>() }
    val actions = listOf("Breathing timer", "Family photos", "Reassurance message", "Call caregiver", "Common question helper")
    CareSectionCard("What Helps") {
            Text("After an action, choose what helped. This is local demo tracking.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            actions.forEach { action ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(action, modifier = Modifier.weight(1f), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Button(onClick = { counts[action] = (counts[action] ?: 0) + 1 }) { Text("Helped") }
                    Spacer(Modifier.width(4.dp))
                    OutlinedButton(onClick = { counts[action] = counts[action] ?: 0 }) { Text("Not sure") }
                }
            }
            val best = counts.maxByOrNull { it.value }
            if (best != null && best.value > 0) {
                Text("Most Helpful This Week: ${best.key} (${best.value} times)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
            }
    }
}

@Composable
fun CommonQuestionCard(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    CareSectionCard(question) {
            Text(
                text = "A simple answer is available when you tap below.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
                Text(if (expanded) "Hide answer" else "Show answer")
            }
            if (expanded) {
                Text("A: $answer", fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("You are safe. Take your time.", fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
            }
    }
}
