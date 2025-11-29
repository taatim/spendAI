package com.example.spendai.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AssistantFab(onPressed: () -> Unit) {
    FloatingActionButton(
        onClick = onPressed,
        containerColor = Color.Black,
        contentColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "AI Assistant"
        )
    }
}
