package com.careapp.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Shows the exact text or file contents the user is about to share. */
@Composable
fun SharePreviewDialog(
    title: String,
    previewText: String,
    onCancel: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Review before sharing") },
        text = {
            Column(Modifier.fillMaxWidth().heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
                Text(title, fontSize = 18.sp)
                Text(previewText, fontSize = 15.sp)
            }
        },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancel") } },
        confirmButton = { Button(onClick = onShare) { Text("Share") } }
    )
}
