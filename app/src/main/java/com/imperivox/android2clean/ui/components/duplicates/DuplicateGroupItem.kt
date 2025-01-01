package com.imperivox.android2clean.ui.components.duplicates

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imperivox.android2clean.data.model.DuplicateGroup
import com.imperivox.android2clean.utils.FileUtils

@Composable
fun DuplicateGroupItem(
    group: DuplicateGroup,
    onFileSelected: (String, Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "${group.files.size} duplicates, ${FileUtils.formatFileSize(group.size)} each",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            group.files.forEach { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = file.isSelected,
                        onCheckedChange = { checked ->
                            onFileSelected(file.path, checked)
                        }
                    )
                    Text(
                        text = file.path,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}