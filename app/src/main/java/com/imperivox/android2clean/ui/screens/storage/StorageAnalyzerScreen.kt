package com.imperivox.android2clean.ui.screens.storage

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imperivox.android2clean.ui.components.storage.StorageChart
import com.imperivox.android2clean.ui.components.storage.StorageTypeList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageAnalyzerScreen(
    onNavigateBack: () -> Unit,
    viewModel: StorageAnalyzerViewModel = viewModel()
) {
    val storageItems by viewModel.storageItems.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.analyzeStorage()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Storage Analyzer") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            if (isAnalyzing) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                StorageChart(
                    items = storageItems.groupBy { it.type }
                        .mapValues { it.value.sumOf { item -> item.size } }
                )

                Spacer(modifier = Modifier.height(16.dp))

                StorageTypeList(storageItems = storageItems)
            }
        }
    }
}