package com.careapp.app.ui.components

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun PullToRefreshBox(onRefresh: () -> Unit, isRefreshing: Boolean = false, content: @Composable () -> Unit) {
    var dragDistance by remember { mutableFloatStateOf(0f) }
    Box(
        Modifier.fillMaxSize().pointerInput(Unit) {
            detectVerticalDragGestures(
                onVerticalDrag = { _, dragAmount -> dragDistance += dragAmount },
                onDragEnd = {
                    if (dragDistance > 100f && !isRefreshing) onRefresh()
                    dragDistance = 0f
                },
                onDragCancel = { dragDistance = 0f }
            )
        }
    ) {
        content()
        if (isRefreshing) CircularProgressIndicator(Modifier.align(Alignment.TopCenter))
    }
}
