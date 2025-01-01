package com.imperivox.android2clean.ui.components.explorer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imperivox.android2clean.data.model.FileItem

enum class FileAction {
    Copy, Move, Delete, Rename
}

@Composable
fun FileList(
    files: List<FileItem>,
    onFileClick: (FileItem) -> Unit,
    onFileAction: (FileItem, FileAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = files,
            key = { it.path }
        ) { file ->
            FileListItem(
                file = file,
                onClick = { onFileClick(file) },
                onAction = { action -> onFileAction(file, action) }
            )
        }
    }
}