package com.imperivox.android2clean.ui.screens.corpses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imperivox.android2clean.ui.components.corpses.AppCorpseItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorpseFinderScreen(
    onNavigateBack: () -> Unit,
    viewModel: CorpseFinderViewModel = viewModel()
) {
    val corpses by viewModel.corpses.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Corpse Finder") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (corpses.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.deleteSelectedCorpses() }
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
            Button(
                onClick = { viewModel.startScan() },
                enabled = !isScanning,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isScanning) "Scanning..." else "Scan for App Corpses")
            }

            if (isScanning) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(corpses) { corpse ->
                    AppCorpseItem(
                        corpse = corpse,
                        onFileSelected = { path, selected ->
                            viewModel.toggleFileSelection(corpse.packageName, path, selected)
                        }
                    )
                }
            }
        }
    }
}