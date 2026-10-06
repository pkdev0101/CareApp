package com.careapp.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careapp.app.data.Medication
import com.careapp.app.ui.theme.*

@Composable
fun MedicationCard(
    medication: Medication,
    onTakeMedicine: () -> Unit,
    onRemindLater: () -> Unit,
    onNeedHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = medication.scheduledTime,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = medication.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Dose: ${medication.dose}", fontSize = 16.sp, color = TextDark)
            Text(text = "Instructions: ${medication.instructions}", fontSize = 14.sp, color = TextGray)
            Text(
                text = "Pill: ${medication.pillDescription}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTakeMedicine,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTealLight),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("I Took It", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
                }

                Button(
                    onClick = onRemindLater,
                    colors = ButtonDefaults.buttonColors(containerColor = BackgroundMedium),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Remind Later", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }

                Button(
                    onClick = onNeedHelp,
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Need Help", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
                }
            }
        }
    }
}
