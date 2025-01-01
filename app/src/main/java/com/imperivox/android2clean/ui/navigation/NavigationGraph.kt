package com.imperivox.android2clean.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.imperivox.android2clean.ui.screens.dashboard.DashboardScreen
import com.imperivox.android2clean.ui.screens.appmanager.AppManagerScreen
import com.imperivox.android2clean.ui.screens.storage.StorageAnalyzerScreen
import com.imperivox.android2clean.ui.screens.cache.CacheCleanerScreen
import com.imperivox.android2clean.ui.screens.explorer.FileExplorerScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAppManager = { navController.navigate(Screen.AppManager.route) },
                onNavigateToStorage = { navController.navigate(Screen.StorageAnalyzer.route) },
                onNavigateToCache = { navController.navigate(Screen.CacheCleaner.route) },
                onNavigateToExplorer = { navController.navigate(Screen.FileExplorer.route) }
            )
        }
        composable(Screen.AppManager.route) {
            AppManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.StorageAnalyzer.route) {
            StorageAnalyzerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.CacheCleaner.route) {
            CacheCleanerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.FileExplorer.route) {
            FileExplorerScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}