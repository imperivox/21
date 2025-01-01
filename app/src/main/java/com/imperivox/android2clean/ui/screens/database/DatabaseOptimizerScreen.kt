package com.imperivox.android2clean.ui.screens.database

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imperivox.android2clean.data.repository.OptimizationResult
import com.imperivox.android2clean.ui.components.database.DatabaseAnalysisView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseOptimizerScreen(
    onNavigateBack: () -> Unit,
    viewModel: DatabaseOptimizerViewModel = viewModel()
) {
    val optimizationState by viewModel.optimizationState.collectAsState()
    val results by viewModel.results.collectAsState()
    val databaseAnalysis by viewModel.databaseAnalysis.collectAsState()

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
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
            }

            item {
                Button(
                    onClick = { viewModel.startOptimization() },
                    enabled = !optimizationState,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (optimizationState) "Optimizing..." else "Start Optimization")
                }
            }

            if (results.isNotEmpty()) {
                item {
                    Text(
                        text = "Results",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(results) { result ->
                    when (result) {
                        is OptimizationResult.Success -> {
                            Card {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "✓ ${result.databaseName}",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    if (result.spaceSaved > 0) {
                                        Text(
                                            text = "Saved ${result.spaceSaved / 1024} KB",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = result.message,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        is OptimizationResult.Error -> {
                            Card {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "✗ ${result.databaseName}",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = result.error,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (databaseAnalysis.isNotEmpty()) {
                item {
                    Text(
                        text = "Database Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(databaseAnalysis) { analysis ->
                    DatabaseAnalysisView(analysis = analysis)
                }
            }
        }
    }
}