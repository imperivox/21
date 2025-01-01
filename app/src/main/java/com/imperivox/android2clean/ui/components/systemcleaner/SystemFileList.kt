package com.imperivox.android2clean.ui.components.systemcleaner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imperivox.android2clean.data.model.SystemFile
import com.imperivox.android2clean.utils.FileUtils

@Composable
fun SystemFileList(
    files: List<SystemFile>,
    onFileSelected: (String, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(files) { file ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = file.path.substringAfterLast("/"),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${FileUtils.formatFileSize(file.size)} - ${file.type}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Checkbox(
                        checked = file.isSelected,
                        onCheckedChange = { checked ->
                            onFileSelected(file.path, checked)
                        }
                    )
                }
            }
        }
    }
}