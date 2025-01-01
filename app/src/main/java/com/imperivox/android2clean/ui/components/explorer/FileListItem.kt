package com.imperivox.android2clean.ui.components.explorer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imperivox.android2clean.data.model.FileItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileListItem(
    file: FileItem,
    onClick: () -> Unit,
    onAction: (FileAction) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                contentDescription = if (file.isDirectory) "Folder" else "File"
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatFileDetails(file),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Copy") },
                    onClick = {
                        onAction(FileAction.Copy)
                        showMenu = false
                    },
                    leadingIcon = { Icon(Icons.Default.ContentCopy, null) }
                )
                DropdownMenuItem(
                    text = { Text("Move") },
                    onClick = {
                        onAction(FileAction.Move)
                        showMenu = false
                    },
                    leadingIcon = { Icon(Icons.Default.DriveFileMove, null) }
                )
                DropdownMenuItem(
                    text = { Text("Rename") },
                    onClick = {
                        onAction(FileAction.Rename)
                        showMenu = false
                    },
                    leadingIcon = { Icon(Icons.Default.Edit, null) }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        onAction(FileAction.Delete)
                        showMenu = false
                    },
                    leadingIcon = { Icon(Icons.Default.Delete, null) }
                )
            }
        }
    }
}

private fun formatFileDetails(file: FileItem): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val size = when {
        file.isDirectory -> ""
        file.size < 1024 -> "${file.size} B"
        file.size < 1024 * 1024 -> "${file.size / 1024} KB"
        else -> "${file.size / (1024 * 1024)} MB"
    }
    return buildString {
        if (size.isNotEmpty()) {
            append(size)
            append(" • ")
        }
        append(sdf.format(file.lastModified))
    }
}