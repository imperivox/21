package com.imperivox.android2clean.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imperivox.android2clean.ui.components.DashboardCard

@Composable
fun DashboardScreen(
    onNavigateToAppManager: () -> Unit,
    onNavigateToStorage: () -> Unit,
    onNavigateToCache: () -> Unit,
    onNavigateToExplorer: () -> Unit,
    onNavigateToDatabase: () -> Unit,
    onNavigateToDuplicates: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val junkFiles by viewModel.junkFiles.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Device Cleaner",
            style = MaterialTheme.typography.headlineMedium
        )

        DashboardCard(
            title = "App Manager",
            subtitle = "Manage and uninstall apps",
            icon = Icons.Default.Apps,
            onClick = onNavigateToAppManager
        )

        DashboardCard(
            title = "Storage Analyzer",
            subtitle = "Analyze storage usage",
            icon = Icons.Default.Storage,
            onClick = onNavigateToStorage
        )

        DashboardCard(
            title = "Cache Cleaner",
            subtitle = "${junkFiles.sumOf { it.size / 1024 / 1024 }} MB cache found",
            icon = Icons.Default.CleaningServices,
            isLoading = isScanning,
            onClick = onNavigateToCache
        )

        DashboardCard(
            title = "File Explorer",
            subtitle = "Browse and manage files",
            icon = Icons.Default.Folder,
            onClick = onNavigateToExplorer
        )

        DashboardCard(
            title = "Database Optimizer",
            subtitle = "Optimize SQLite databases",
            icon = Icons.Default.Build,
            onClick = onNavigateToDatabase
        )
        DashboardCard(
            title = "Duplicate Finder",
            subtitle = "Find duplicate files",
            icon = Icons.Default.Filter,
            onClick = onNavigateToDuplicates
        )
    }
}