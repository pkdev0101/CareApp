package com.careapp.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careapp.app.data.SampleData
import com.careapp.app.ui.components.MedicationCard
import com.careapp.app.ui.theme.*
import com.careapp.app.viewmodel.MedicationViewModel

@Composable
fun MedicationScreen(viewModel: MedicationViewModel? = null) {
    // Use ViewModel if available, otherwise fall back to SampleData
    val medications by (viewModel?.medications?.collectAsState() ?: remember { mutableStateOf(SampleData.medications) })
    val showDialog by (viewModel?.showDialog?.collectAsState() ?: remember { mutableStateOf(false) })
    val dialogTitle by (viewModel?.dialogTitle?.collectAsState() ?: remember { mutableStateOf("") })
    val dialogMessage by (viewModel?.dialogMessage?.collectAsState() ?: remember { mutableStateOf("") })
    val showDuplicateWarning by (viewModel?.showDuplicateWarning?.collectAsState() ?: remember { mutableStateOf(false) })
    val duplicateWarningTime by (viewModel?.duplicateWarningTime?.collectAsState() ?: remember { mutableStateOf("") })

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Text(
                text = "My Medicine",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        item {
            Text(
                text = "Today's Schedule",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items(medications) { medication ->
            MedicationCard(
                medication = medication,
                onTakeMedicine = {
                    viewModel?.takeMedication(medication.id, medication)
                },
                onRemindLater = {
                    viewModel?.remindLater(medication.id, medication)
                },
                onNeedHelp = {
                    viewModel?.needHelp(medication.id, medication)
                }
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Dialog: Confirmation/Success
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel?.dismissDialog() },
            title = { Text(dialogTitle, fontWeight = FontWeight.Bold) },
            text = { Text(dialogMessage, fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = { viewModel?.dismissDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTealLight)
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Dialog: Duplicate Warning
    if (showDuplicateWarning) {
        AlertDialog(
            onDismissRequest = { viewModel?.dismissDuplicateWarning() },
            title = { Text("Already Taken", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This medication was already marked taken at $duplicateWarningTime.\n\n" +
                    "Ask your caregiver before taking it again.",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel?.dismissDuplicateWarning() },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
                ) {
                    Text("I Understand", color = TextDark, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
