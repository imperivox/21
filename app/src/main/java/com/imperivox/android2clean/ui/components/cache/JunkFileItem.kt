package com.imperivox.android2clean.ui.components.cache

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imperivox.android2clean.data.model.JunkFile

@Composable
fun JunkFileItem(file: JunkFile) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = file.path.substringAfterLast("/"),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${file.size / 1024} KB - ${file.type}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}