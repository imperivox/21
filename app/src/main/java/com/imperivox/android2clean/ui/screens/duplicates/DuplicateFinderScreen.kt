package com.imperivox.android2clean.ui.screens.duplicates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imperivox.android2clean.ui.components.duplicates.DuplicateGroupItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuplicateFinderScreen(
    onNavigateBack: () -> Unit,
    viewModel: DuplicateFinderViewModel = viewModel()
) {
    val duplicateGroups by viewModel.duplicateGroups.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val quickScan by viewModel.quickScan.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Duplicate Finder") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (duplicateGroups.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.deleteSelectedDuplicates() }
                        ) {
                            Icon(Icons.Default.Delete, "Delete selected")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = quickScan,
                    onCheckedChange = { viewModel.setQuickScan(it) }
                )
                Text(
                    text = "Quick scan",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Button(
                onClick = { viewModel.startScan() },
                enabled = !isScanning,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(if (isScanning) "Scanning..." else "Start Scan")
            }

            if (isScanning) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(duplicateGroups) { group ->
                    DuplicateGroupItem(
                        group = group,
                        onFileSelected = { path, selected ->
                            viewModel.toggleFileSelection(path, selected)
                        }
                    )
                }
            }
        }
    }
}