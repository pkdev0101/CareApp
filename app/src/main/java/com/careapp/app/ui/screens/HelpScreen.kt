package com.careapp.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.careapp.app.data.SampleData
import com.careapp.app.ui.CareAppScreen
import com.careapp.app.ui.components.LargeButton
import com.careapp.app.ui.theme.*
import com.careapp.app.viewmodel.EmergencyViewModel
import com.careapp.app.viewmodel.LocationViewModel

@Composable
fun HelpScreen(navController: NavController? = null, emergencyViewModel: EmergencyViewModel? = null, locationViewModel: LocationViewModel? = null) {
    LazyColumn(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp), contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item { Text("Help Me", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal); Text("Choose memory help or urgent help. If unsure, call your caregiver.", fontSize = 16.sp, modifier = Modifier.padding(bottom = 6.dp)) }
        item { Text("Help Me Remember", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SupportPlum) }
        item { LargeButton("What was I doing?", { navController?.navigate(CareAppScreen.CalmMode.name) }, backgroundColor = SupportPlumLight) }
        item { LargeButton("What do I do next?", { navController?.navigate(CareAppScreen.Routine.name) }, backgroundColor = SupportPlumLight) }
        item { LargeButton("Did I take my medicine?", { navController?.navigate(CareAppScreen.Medication.name) }, backgroundColor = PrimaryTealLight) }
        item { LargeButton("Who can I call?", { navController?.navigate(CareAppScreen.Family.name) }, backgroundColor = SecondaryCoral) }
        item { Text("Urgent Help", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DangerRed, modifier = Modifier.padding(top = 8.dp)) }
        item { LargeButton("I May Be Lost — show location", { navController?.navigate(CareAppScreen.Lost.name) }, backgroundColor = SupportPlum) }
        item { LargeButton("Emergency Help", { navController?.navigate(CareAppScreen.Emergency.name) }, backgroundColor = DangerRed) }
        item { LargeButton("Emergency Card & QR", { navController?.navigate(CareAppScreen.QRCode.name) }, backgroundColor = SecondaryCoral) }
    }
}
