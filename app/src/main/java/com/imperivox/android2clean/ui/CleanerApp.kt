package com.imperivox.android2clean.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.imperivox.android2clean.ui.navigation.NavigationGraph
import com.imperivox.android2clean.ui.theme.Android2CleanTheme

@Composable
fun CleanerApp() {
    val navController = rememberNavController()

    Android2CleanTheme {
        NavigationGraph(navController = navController)
    }
}