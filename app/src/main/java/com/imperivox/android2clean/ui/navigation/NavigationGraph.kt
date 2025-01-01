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
import com.imperivox.android2clean.ui.screens.database.DatabaseOptimizerScreen
import com.imperivox.android2clean.ui.screens.duplicates.DuplicateFinderScreen
import com.imperivox.android2clean.ui.screens.systemcleaner.SystemCleanerScreen
import com.imperivox.android2clean.ui.screens.corpses.CorpseFinderScreen

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
                onNavigateToExplorer = { navController.navigate(Screen.FileExplorer.route) },
                onNavigateToDatabase = { navController.navigate(Screen.DatabaseOptimizer.route) },
                onNavigateToDuplicates = { navController.navigate(Screen.DuplicateFinder.route) },
                onNavigateToSystemCleaner = { navController.navigate(Screen.SystemCleaner.route) },
                onNavigateToCorpseFinder = { navController.navigate(Screen.CorpseFinder.route) }
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
        composable(Screen.DatabaseOptimizer.route) {
            DatabaseOptimizerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.DuplicateFinder.route) {
            DuplicateFinderScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.SystemCleaner.route) {
            SystemCleanerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.CorpseFinder.route) {
            CorpseFinderScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
