package com.imperivox.android2clean.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object AppManager : Screen("app_manager")
    object StorageAnalyzer : Screen("storage_analyzer")
    object CacheCleaner : Screen("cache_cleaner")
    object FileExplorer : Screen("file_explorer")
    object DatabaseOptimizer : Screen("database_optimizer")
    object DuplicateFinder : Screen("duplicate_finder")
    object SystemCleaner : Screen("system_cleaner")
}