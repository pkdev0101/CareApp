package com.careapp.app.ui.screens

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import com.careapp.app.data.SampleData
import com.careapp.app.ui.CareAppScreen
import com.careapp.app.viewmodel.MemoryCardViewModel
import com.careapp.app.ui.components.LargeButton
import com.careapp.app.ui.theme.*

@Composable
fun CalmModeScreen(navController: NavController? = null, viewModel: MemoryCardViewModel? = null) {
    val senior = SampleData.seniorProfile
    val context = LocalContext.current
    var showBreathingTimer by remember { mutableStateOf(false) }
    var breathPhase by remember { mutableStateOf("inhale") }
    val nextMedication = SampleData.medications.firstOrNull()
    val nextTask = "Take morning medicine"

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
                answer = "You were getting ready for your day. Your next step is: ${nextTask.lowercase()}."
            )
        }

        item {
            ContextRecoveryCard(
                title = "What do I do next?",
                answer = "Next: $nextTask. Take your time and ask your caregiver if you are unsure.",
                actionLabel = "Open Medicine",
                onAction = { navController?.navigate(CareAppScreen.Medication.name) }
            )
        }

        item {
            ContextRecoveryCard(
                title = "Did I take my medicine?",
                answer = "Your next medicine is ${nextMedication?.name ?: "not listed"} at ${nextMedication?.scheduledTime ?: "a scheduled time"}. It has not been confirmed as taken here. Please ask your caregiver before taking medicine if you are unsure.",
                actionLabel = "Open Medicine",
                onAction = { navController?.navigate(CareAppScreen.Medication.name) }
            )
        }

        item {
            ContextRecoveryCard(
                title = "Where am I?",
                answer = "You are at home. You are safe. This is local demo information.",
                actionLabel = "Open Help",
                onAction = { navController?.navigate(CareAppScreen.Help.name) }
            )
        }

        item {
            ContextRecoveryCard(
                title = "Who can I call?",
                answer = "You can call ${senior.primaryCaregiverName}, your caregiver.",
                actionLabel = "Call Caregiver",
                onAction = {
                    runCatching {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = android.net.Uri.parse("tel:${senior.primaryCaregiverPhone}")
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
                    Text("Take your time. ${senior.memoryNote}", fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
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
            Text(
                text = "Common Questions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        item {
            CommonQuestionCard("Where am I?", "You are at ${senior.address}. You are safe.")
        }

        item {
            CommonQuestionCard("Did I take my medicine?", "Check the Medicine screen for today's medication status. If you are unsure, please call your caregiver.")
        }

        item {
            CommonQuestionCard("Who is visiting today?", "${senior.primaryCaregiverName} may visit today. Take your time and you can call your caregiver.")
        }

        item {
            CommonQuestionCard("What should I do next?", "Your next routine step is to drink water and take your time.")
        }

        item {
            CommonQuestionCard("Who can I call?", "You can call ${senior.primaryCaregiverName} at ${senior.primaryCaregiverPhone}.")
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
                            data = android.net.Uri.parse("tel:${senior.primaryCaregiverPhone}")
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
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
            Spacer(modifier = Modifier.height(4.dp))
            if (expanded) {
                Text(answer, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
                actionLabel?.let { label ->
                    Button(
                        onClick = { onAction?.invoke() },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(top = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTealLight)
                    ) {
                        Text(label, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
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
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F0FA))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("What Helps", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SupportPlum)
            Text("After an action, choose what helped. This is local demo tracking.", fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 6.dp))
            actions.forEach { action ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(action, modifier = Modifier.weight(1f), fontSize = 14.sp)
                    Button(onClick = { counts[action] = (counts[action] ?: 0) + 1 }, colors = ButtonDefaults.buttonColors(containerColor = SuccessTeal)) { Text("Helped") }
                    Spacer(Modifier.width(4.dp))
                    Button(onClick = { counts[action] = counts[action] ?: 0 }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) { Text("Not sure") }
                }
            }
            val best = counts.maxByOrNull { it.value }
            if (best != null && best.value > 0) {
                Text("Most Helpful This Week: ${best.key} (${best.value} times)", fontWeight = FontWeight.Bold, color = PrimaryTeal, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
fun CommonQuestionCard(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Q: $question",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { expanded = !expanded }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryTealLight)) {
                Text(if (expanded) "Hide answer" else "Show answer")
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("A: $answer", fontSize = 17.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                Text("You are safe. Take your time.", fontSize = 15.sp, color = PrimaryTeal)
            }
        }
    }
}
