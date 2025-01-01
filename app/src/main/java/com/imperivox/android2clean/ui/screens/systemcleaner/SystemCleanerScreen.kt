package com.imperivox.android2clean.ui.screens.systemcleaner

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imperivox.android2clean.ui.components.systemcleaner.CleaningRuleItem
import com.imperivox.android2clean.ui.components.systemcleaner.SystemFileList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemCleanerScreen(
    onNavigateBack: () -> Unit,
    viewModel: SystemCleanerViewModel = viewModel()
) {
    val rules by viewModel.rules.collectAsState()
    val systemFiles by viewModel.systemFiles.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Cleaner") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (systemFiles.isNotEmpty()) {
                        IconButton(onClick = { viewModel.cleanSelectedFiles() }) {
                            Icon(Icons.Default.Delete, "Clean selected")
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
            Text(
                text = "Cleaning Rules",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            rules.forEach { rule ->
                CleaningRuleItem(
                    rule = rule,
                    onRuleToggled = { enabled ->
                        viewModel.toggleRule(rule, enabled)
                    }
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

            if (systemFiles.isNotEmpty()) {
                Text(
                    text = "Found Files",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                SystemFileList(
                    files = systemFiles,
                    onFileSelected = { path, selected ->
                        viewModel.toggleFileSelection(path, selected)
                    }
                )
            }
        }
    }
}