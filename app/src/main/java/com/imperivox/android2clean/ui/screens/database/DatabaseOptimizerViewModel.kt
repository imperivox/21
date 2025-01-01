package com.imperivox.android2clean.ui.screens.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imperivox.android2clean.data.repository.DatabaseOptimizerRepository
import com.imperivox.android2clean.data.repository.OptimizationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DatabaseOptimizerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DatabaseOptimizerRepository(application)

    private val _optimizationState = MutableStateFlow(false)
    val optimizationState: StateFlow<Boolean> = _optimizationState

    private val _results = MutableStateFlow<List<OptimizationResult>>(emptyList())
    val results: StateFlow<List<OptimizationResult>> = _results

    fun startOptimization() {
        viewModelScope.launch {
            _optimizationState.value = true
            val resultsList = mutableListOf<OptimizationResult>()

            repository.optimizeDatabases()
                .collect { result ->
                    resultsList.add(result)
                    _results.value = resultsList.toList()
                }

            _optimizationState.value = false
        }
    }
}