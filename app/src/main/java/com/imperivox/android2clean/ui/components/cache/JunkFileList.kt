package com.imperivox.android2clean.ui.components.cache

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imperivox.android2clean.data.model.JunkFile

@Composable
fun JunkFileList(
    files: List<JunkFile>,
    isCleaning: Boolean,
    onCleanClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Total size: ${files.sumOf { it.size } / 1024 / 1024} MB",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(files) { file ->
                JunkFileItem(file = file)
            }
        }

        Button(
            onClick = onCleanClick,
            enabled = !isCleaning,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(if (isCleaning) "Cleaning..." else "Clean Selected")
        }
    }
}