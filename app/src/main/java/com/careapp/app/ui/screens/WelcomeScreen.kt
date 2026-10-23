package com.careapp.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    hasSavedProgress: Boolean,
    onSetup: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to CareApp", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(
            "Helping you remember, stay safe, and stay connected.",
            fontSize = 19.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 12.dp, bottom = 32.dp)
        )
        Button(onClick = onSetup, modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (hasSavedProgress) "Resume Caregiver Setup" else "Set Up With a Caregiver", fontSize = 18.sp)
                Text("A trusted family member or caregiver can prepare CareApp for everyday use.", fontSize = 13.sp)
            }
        }
    }
}
