package com.careapp.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careapp.app.ui.theme.*

@Composable
fun AdherenceWidget(
    percentage: Int = 86,
    medicationsTaken: Int = 2,
    medicationsTotal: Int = 3
) {
    val color = when {
        percentage >= 90 -> SuccessTeal
        percentage >= 70 -> WarningAmber
        else -> ErrorRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Medication Adherence",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal
                )
                Box(
                    modifier = Modifier
                        .background(color, shape = RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$percentage%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { percentage.toFloat() / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = color,
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Statistics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "This Week",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$medicationsTaken/$medicationsTotal taken",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryTeal
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Status",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = when {
                            percentage >= 90 -> "✓ Excellent"
                            percentage >= 70 -> "○ Good"
                            else -> "✗ Needs Attention"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = color
                    )
                }
            }
        }
    }
}
