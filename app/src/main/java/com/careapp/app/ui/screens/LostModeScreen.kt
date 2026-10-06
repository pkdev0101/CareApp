package com.careapp.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.careapp.app.data.SampleData
import com.careapp.app.ui.CareAppScreen
import com.careapp.app.ui.components.LargeButton
import com.careapp.app.ui.theme.*
import com.careapp.app.viewmodel.LocationViewModel

@Composable
fun LostModeScreen(viewModel: LocationViewModel? = null, navController: NavController? = null) {
    val senior = SampleData.seniorProfile
    val context = LocalContext.current
    val latitude by (viewModel?.currentLatitude?.collectAsState() ?: remember { mutableStateOf(0.0) })
    val longitude by (viewModel?.currentLongitude?.collectAsState() ?: remember { mutableStateOf(0.0) })
    val timestamp by (viewModel?.locationTimestamp?.collectAsState() ?: remember { mutableStateOf("") })
    val isWithinSafeZone by (viewModel?.isWithinSafeZone?.collectAsState() ?: remember { mutableStateOf(true) })
    val shareText by (viewModel?.shareLocationText?.collectAsState() ?: remember { mutableStateOf("") })
    val showDialog by (viewModel?.showDialog?.collectAsState() ?: remember { mutableStateOf(false) })
    val dialogTitle by (viewModel?.dialogTitle?.collectAsState() ?: remember { mutableStateOf("") })
    val dialogMessage by (viewModel?.dialogMessage?.collectAsState() ?: remember { mutableStateOf("") })

    LaunchedEffect(Unit) { viewModel?.getCurrentLocation() }

    fun shareLocation() {
        if (shareText.isBlank()) return
        val intent = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, shareText) }
        context.startActivity(Intent.createChooser(intent, "Share My Location"))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("I may be lost", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text("My name is ${senior.preferredName}. I may have memory loss.", fontSize = 17.sp, modifier = Modifier.padding(bottom = 4.dp))
        }
        item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Current location", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    if (timestamp.isBlank()) {
                        Text("Location is not available yet.", fontSize = 16.sp, color = TextGray)
                    } else {
                        Text("Latitude: %.5f".format(latitude), fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Longitude: %.5f".format(longitude), fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Last updated: $timestamp", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Status: Demo location", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(if (isWithinSafeZone) "Inside Safe Zone" else "Outside Safe Zone", fontWeight = FontWeight.Bold, color = if (isWithinSafeZone) SuccessTeal else WarningAmber, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
        item { LargeButton("Refresh Location", { viewModel?.getCurrentLocation() }, backgroundColor = MaterialTheme.colorScheme.primary, textColor = MaterialTheme.colorScheme.onPrimary) }
        item { LargeButton("Share My Location", { shareLocation() }, backgroundColor = MaterialTheme.colorScheme.primary, textColor = MaterialTheme.colorScheme.onPrimary) }
        item {
            LargeButton("Call Caregiver\n${senior.primaryCaregiverName}", {
                runCatching { context.startActivity(Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:${senior.primaryCaregiverPhone}"))) }
            }, backgroundColor = MaterialTheme.colorScheme.primary, textColor = MaterialTheme.colorScheme.onPrimary)
        }
        item { LargeButton("Emergency Card & QR", { navController?.navigate(CareAppScreen.QRCode.name) }, backgroundColor = MaterialTheme.colorScheme.secondary, textColor = MaterialTheme.colorScheme.onSecondary) }
        item { Button(onClick = { viewModel?.markAsSafe() }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("I'm Safe") } }
        item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(14.dp)) {
                    Text("If you found me", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Please stay with me and call my caregiver.", fontSize = 16.sp)
                }
            }
        }
    }

    if (showDialog) AlertDialog(
        onDismissRequest = { viewModel?.dismissDialog() },
        title = { Text(dialogTitle) },
        text = { Text(dialogMessage) },
        confirmButton = { Button(onClick = { viewModel?.dismissDialog() }) { Text("OK") } }
    )
}
