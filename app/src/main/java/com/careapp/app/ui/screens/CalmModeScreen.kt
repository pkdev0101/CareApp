package com.careapp.app.ui.screens

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.careapp.app.ui.CareAnchorScreen
import com.careapp.app.viewmodel.MemoryCardViewModel
import com.careapp.app.ui.components.LargeButton
import com.careapp.app.ui.theme.*

@Composable
fun CalmModeScreen(navController: NavController? = null, viewModel: MemoryCardViewModel? = null) {
    val senior = SampleData.seniorProfile
    val context = LocalContext.current
    var showBreathingTimer by remember { mutableStateOf(false) }
    var breathPhase by remember { mutableStateOf("inhale") }

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
            .background(BackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "I Feel Confused",
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You are safe.",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your name is ${senior.preferredName}.",
                        fontSize = 20.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = senior.memoryNote,
                        fontSize = 18.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

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

        // Quick Info Cards
        item {
            Text(
                text = "Helpful Reminders",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        item {
            QuickInfoCard("Where am I?", "You are at home. You are safe.")
        }

        item {
            QuickInfoCard("Did I take my medicine?", "Yes, you took your medicine at 8:00 AM.")
        }

        item {
            QuickInfoCard("Who is visiting today?", "Maya will visit at 5:00 PM.")
        }

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
                text = "Show Family Photos",
                onClick = { navController?.navigate(CareAnchorScreen.Family.name) },
                backgroundColor = SecondaryCoral
            )
        }

        item {
            LargeButton(
                text = "Emergency Help",
                onClick = { navController?.navigate(CareAnchorScreen.Emergency.name) },
                backgroundColor = DangerRed
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun QuickInfoCard(question: String, answer: String) {
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
            Text(
                text = "A: $answer",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }
    }
}
