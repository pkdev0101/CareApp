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
import com.careapp.app.ui.theme.*
import com.careapp.app.viewmodel.AccessibilityViewModel

@Composable
fun SettingsScreen(viewModel: AccessibilityViewModel? = null) {
    val textSizeScale by (viewModel?.textSizeScale?.collectAsState() ?: remember { mutableStateOf(1f) })
    val highContrastEnabled by (viewModel?.highContrastEnabled?.collectAsState() ?: remember { mutableStateOf(false) })

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Text(
                text = "Settings",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        // Text Size Setting
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📝 Text Size",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel?.setTextSize(0.9f) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (textSizeScale == 0.9f) PrimaryTealLight else Color(0xFFE0E0E0)
                            ),
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) {
                            Text("Small", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { viewModel?.setTextSize(1f) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (textSizeScale == 1f) PrimaryTealLight else Color(0xFFE0E0E0)
                            ),
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                        ) {
                            Text("Normal", fontSize = 14.sp)
                        }
                        Button(
                            onClick = { viewModel?.setTextSize(1.2f) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (textSizeScale == 1.2f) PrimaryTealLight else Color(0xFFE0E0E0)
                            ),
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) {
                            Text("Large", fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Current: ${(textSizeScale * 100).toInt()}%",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // High Contrast Setting
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎨 High Contrast Mode",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                    Switch(
                        checked = highContrastEnabled,
                        onCheckedChange = { viewModel?.setHighContrast(it) },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        // About Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ℹ️ About CareApp",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Version 1.0",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "A memory care safety app for seniors with Alzheimer's and dementia.",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "© 2026 CareApp",
                        fontSize = 12.sp,
                        color = TextLightGray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
