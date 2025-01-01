package com.imperivox.android2clean.ui.components.corpses

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imperivox.android2clean.data.model.AppCorpse
import com.imperivox.android2clean.utils.FileUtils
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AppCorpseItem(
    corpse: AppCorpse,
    onFileSelected: (String, Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = corpse.packageName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Total size: ${FileUtils.formatFileSize(corpse.totalSize)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Last modified: ${dateFormat.format(Date(corpse.lastModified))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Hide Files" else "Show Files")
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                corpse.files.forEach { file ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = file.isSelected,
                            onCheckedChange = { selected ->
                                onFileSelected(file.path, selected)
                            }
                        )
                        Column {
                            Text(
                                text = file.path.substringAfterLast("/"),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${FileUtils.formatFileSize(file.size)} - ${file.type}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}