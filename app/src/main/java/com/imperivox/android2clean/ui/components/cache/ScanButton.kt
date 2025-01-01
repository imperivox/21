package com.imperivox.android2clean.ui.components.cache

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScanButton(
    isScanning: Boolean,
    onScanClick: () -> Unit
) {
    Button(
        onClick = onScanClick,
        enabled = !isScanning,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(if (isScanning) "Scanning..." else "Start Scan")
    }
}