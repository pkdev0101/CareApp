package com.careapp.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careapp.app.ui.theme.*

@Composable
fun SafetyReadinessScreen() {
    val checks = listOf(
        "Emergency contact added" to true,
        "Medication list complete" to true,
        "Allergies entered" to true,
        "Medical conditions filled" to true,
        "Emergency card ready" to true,
        "Location permission enabled" to false,
        "Battery above 25%" to true,
        "Care Snapshot created" to false
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Text(
                text = "Safety Readiness",
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
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryTealVeryLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Readiness Status",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val passCount = checks.count { it.second }
                    Text(
                        text = "$passCount/${checks.size} items ready",
                        fontSize = 16.sp,
                        color = PrimaryTeal
                    )
                    LinearProgressIndicator(
                        progress = { passCount.toFloat() / checks.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        color = SuccessTeal,
                        trackColor = Color(0xFFCCCCCC)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        checks.forEach { (label, isPassed) ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPassed) Color.White else Color(0xFFFFFBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (isPassed) "✓" else "!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPassed) SuccessTeal else WarningAmber
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
