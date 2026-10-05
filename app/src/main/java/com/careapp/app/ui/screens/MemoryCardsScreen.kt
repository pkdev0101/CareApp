package com.careapp.app.ui.screens

import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careapp.app.data.MemoryCard
import com.careapp.app.data.SampleData
import com.careapp.app.ui.theme.*

@Composable
fun MemoryCardsScreen(viewModel: com.careapp.app.viewmodel.MemoryCardViewModel? = null) {
    val context = LocalContext.current
    val memoryCards by (viewModel?.memoryCards?.collectAsState() ?: remember { mutableStateOf(SampleData.memoryCards) })
    var selectedContactName by remember { mutableStateOf("") }
    var selectedContactPhone by remember { mutableStateOf("") }

    // Contacts picker launcher
    val contactPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickContact()
    ) { contactUri ->
        if (contactUri != null) {
            try {
                val cursor = context.contentResolver.query(contactUri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIdx = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        selectedContactName = if (nameIdx >= 0) it.getString(nameIdx) else ""
                    }
                }

                // Get phone number
                val phoneCursor = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(contactUri.lastPathSegment),
                    null
                )
                phoneCursor?.use {
                    if (it.moveToFirst()) {
                        val phoneIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        selectedContactPhone = if (phoneIdx >= 0) it.getString(phoneIdx) else ""
                    }
                }
            } catch (e: Exception) {
                // Contact read failed
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "People",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryCoral
                )
                Button(
                    onClick = { contactPickerLauncher.launch(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTealLight),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Text("➕ Add", fontSize = 12.sp)
                }
            }
        }

        item {
            Text(
                text = "Family, caregivers, doctors, and helpers",
                fontSize = 18.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (selectedContactName.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "✓ New contact added",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = selectedContactName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryCoral
                        )
                        if (selectedContactPhone.isNotEmpty()) {
                            Text(
                                text = selectedContactPhone,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        items(memoryCards) { card ->
            MemoryCardItem(card)
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun MemoryCardItem(card: MemoryCard) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "This is ${card.name}.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SecondaryCoral
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "She is your ${card.relationship}.",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = card.memoryCue,
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "She helps with ${card.helpsWith}.",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = InfoTeal
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Phone: ${card.phone}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                modifier = Modifier.clickable {
                    // Open phone dialer when tapping phone number
                    try {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = android.net.Uri.parse("tel:${card.phone}")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Silently fail if no dialer app
                    }
                }
            )
        }
    }
}
