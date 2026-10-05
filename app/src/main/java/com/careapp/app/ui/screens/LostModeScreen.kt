package com.careapp.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.careapp.app.data.SampleData
import com.careapp.app.ui.CareAppScreen
import com.careapp.app.ui.components.LargeButton
import com.careapp.app.ui.theme.*

@Composable
fun LostModeScreen(viewModel: com.careapp.app.viewmodel.LocationViewModel? = null, navController: NavController? = null) {
    val senior = SampleData.seniorProfile
    val context = LocalContext.current
    val home = SampleData.savedPlaces.firstOrNull { it.type == "Home" }
    val latitude by (viewModel?.currentLatitude?.collectAsState() ?: remember { mutableStateOf(0.0) })
    val longitude by (viewModel?.currentLongitude?.collectAsState() ?: remember { mutableStateOf(0.0) })
    val timestamp by (viewModel?.locationTimestamp?.collectAsState() ?: remember { mutableStateOf("") })
    val isWithinSafeZone by (viewModel?.isWithinSafeZone?.collectAsState() ?: remember { mutableStateOf(true) })
    val showDialog by (viewModel?.showDialog?.collectAsState() ?: remember { mutableStateOf(false) })
    val dialogTitle by (viewModel?.dialogTitle?.collectAsState() ?: remember { mutableStateOf("") })
    val dialogMessage by (viewModel?.dialogMessage?.collectAsState() ?: remember { mutableStateOf("") })

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SupportPlumVeryLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "I May Be Lost",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = SupportPlum,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Current location", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    Text(if (timestamp.isBlank()) "No location update yet. This demo uses simulated coordinates." else "Demo location updated at $timestamp", fontSize = 15.sp, color = Color.DarkGray)
                    if (timestamp.isNotBlank()) Text("${"%.5f".format(latitude)}, ${"%.5f".format(longitude)}", fontSize = 18.sp, color = InfoTeal)
                    Text(if (isWithinSafeZone) "Inside Safe Zone" else "Outside Safe Zone", fontWeight = FontWeight.Bold, color = if (isWithinSafeZone) SuccessTeal else WarningAmber, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F1F5))) {
                Column(Modifier.padding(16.dp)) {
                    Text("Safe places", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    SampleData.savedPlaces.forEach { Text("• ${it.name} (${it.type})", fontSize = 15.sp, modifier = Modifier.padding(top = 4.dp)) }
                    Text("Map preview — demo mode", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SupportPlumVeryLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "My name is ${senior.fullName}.",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SupportPlum
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = senior.memoryNote,
                        fontSize = 18.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Please call my caregiver:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = senior.primaryCaregiverName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )

                    Text(
                        text = senior.primaryCaregiverPhone,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DangerRed
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "My home address is:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = senior.address,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = InfoTeal
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Please stay with me until help arrives.",
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        item {
            Text(
                text = "What can I do?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SupportPlum,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        item {
            LargeButton(
                text = "Show Current Location",
                onClick = { viewModel?.getCurrentLocation() },
                backgroundColor = InfoTeal
            )
        }

        item {
            LargeButton(
                text = "Call Caregiver\n${senior.primaryCaregiverName}",
                onClick = {
                    // Open phone dialer with caregiver number (no permission needed, never dials)
                    try {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = android.net.Uri.parse("tel:${senior.primaryCaregiverPhone}")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Silently fail if no dialer app
                    }
                },
                backgroundColor = PrimaryTealLight
            )
        }

        item {
            LargeButton(
                text = "Show Emergency Card",
                onClick = { navController?.navigate(CareAppScreen.Emergency.name) },
                backgroundColor = AccentAmber
            )
        }

        item {
            LargeButton(
                text = "Share My Location",
                onClick = { viewModel?.shareLocation() },
                backgroundColor = InfoTeal
            )
        }

        if (timestamp.isNotBlank()) item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Location found at $timestamp", fontWeight = FontWeight.Bold)
                    Text("${"%.5f".format(latitude)}, ${"%.5f".format(longitude)}", fontSize = 18.sp, color = InfoTeal)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel?.markAsSafe() }) { Text("I’m Safe") }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    if (showDialog) androidx.compose.material3.AlertDialog(
        onDismissRequest = { viewModel?.dismissDialog() },
        title = { Text(dialogTitle) },
        text = { Text(dialogMessage) },
        confirmButton = { androidx.compose.material3.Button(onClick = { viewModel?.dismissDialog() }) { Text("OK") } }
    )
}
