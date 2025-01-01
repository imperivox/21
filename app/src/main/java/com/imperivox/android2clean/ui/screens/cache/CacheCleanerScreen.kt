package com.imperivox.android2clean.ui.screens.cache

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imperivox.android2clean.ui.components.cache.JunkFileList
import com.imperivox.android2clean.ui.components.cache.ScanButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CacheCleanerScreen(
    onNavigateBack: () -> Unit,
    viewModel: CacheCleanerViewModel = viewModel()
) {
    val junkFiles by viewModel.junkFiles.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val isCleaning by viewModel.isCleaning.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cache Cleaner") },
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
            ScanButton(
                isScanning = isScanning,
                onScanClick = { viewModel.scanJunkFiles() }
            )

            if (!isScanning && junkFiles.isNotEmpty()) {
                JunkFileList(
                    files = junkFiles,
                    isCleaning = isCleaning,
                    onCleanClick = { viewModel.cleanSelectedFiles() }
                )
            }
        }
    }
}