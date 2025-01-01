package com.imperivox.android2clean.ui.screens.database

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imperivox.android2clean.data.repository.OptimizationResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseOptimizerScreen(
    onNavigateBack: () -> Unit,
    viewModel: DatabaseOptimizerViewModel = viewModel()
) {
    val optimizationState by viewModel.optimizationState.collectAsState()
    val results by viewModel.results.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Database Optimizer") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Database Optimization",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Optimize SQLite databases to improve performance and reduce storage space.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = { viewModel.startOptimization() },
                enabled = !optimizationState,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (optimizationState) "Optimizing..." else "Start Optimization")
            }

            if (results.isNotEmpty()) {
                Text(
                    text = "Results",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        results.forEach { result ->
                            when (result) {
                                is OptimizationResult.Success -> {
                                    Text(
                                        text = "✓ ${result.databaseName} optimized successfully",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                is OptimizationResult.Error -> {
                                    Text(
                                        text = "✗ ${result.databaseName}: ${result.error}",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}