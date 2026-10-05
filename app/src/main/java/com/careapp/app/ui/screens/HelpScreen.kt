package com.careapp.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavController
import com.careapp.app.data.SampleData
import com.careapp.app.ui.CareAppScreen
import com.careapp.app.ui.components.LargeButton
import com.careapp.app.ui.theme.BackgroundLight
import com.careapp.app.ui.theme.DangerRed
import com.careapp.app.ui.theme.PrimaryTeal
import com.careapp.app.ui.theme.PrimaryTealLight
import com.careapp.app.ui.theme.SecondaryCoral
import com.careapp.app.ui.theme.SupportPlumLight
import com.careapp.app.viewmodel.EmergencyViewModel

@Composable
fun HelpScreen(navController: NavController? = null, emergencyViewModel: EmergencyViewModel? = null) {
    val context = LocalContext.current
    val senior = SampleData.seniorProfile

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("How can we help?", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal, modifier = Modifier.padding(vertical = 12.dp))
            Text("If you are unsure, call your caregiver.", fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))
        }
        item { LargeButton("Emergency Help", { navController?.navigate(CareAppScreen.Emergency.name) }, backgroundColor = DangerRed) }
        item { LargeButton("I Am Lost", { navController?.navigate(CareAppScreen.Lost.name) }, backgroundColor = SupportPlumLight) }
        item {
            LargeButton(
                "Call Caregiver",
                {
                    runCatching { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${senior.primaryCaregiverPhone}"))) }
                },
                backgroundColor = PrimaryTealLight
            )
        }
        item {
            LargeButton(
                "Show Emergency Card",
                {
                    emergencyViewModel?.showEmergencyCard()
                    navController?.navigate(CareAppScreen.Emergency.name)
                },
                backgroundColor = SecondaryCoral
            )
        }
    }
}
