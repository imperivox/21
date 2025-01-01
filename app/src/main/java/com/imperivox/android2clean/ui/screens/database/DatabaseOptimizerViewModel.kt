package com.imperivox.android2clean.ui.screens.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imperivox.android2clean.data.repository.DatabaseAnalysis
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

    private val _databaseAnalysis = MutableStateFlow<List<DatabaseAnalysis>>(emptyList())
    val databaseAnalysis: StateFlow<List<DatabaseAnalysis>> = _databaseAnalysis

    fun startOptimization() {
        viewModelScope.launch {
            _optimizationState.value = true
            val resultsList = mutableListOf<OptimizationResult>()

            repository.optimizeDatabases()
                .collect { result ->
                    resultsList.add(result)
                    _results.value = resultsList.toList()

                    // Analyze the database after optimization
                    if (result is OptimizationResult.Success) {
                        val dbPath = "${getApplication<Application>().applicationInfo.dataDir}/databases/${result.databaseName}"
                        repository.analyzeDatabase(dbPath)
                            .collect { analysis ->
                                _databaseAnalysis.value += analysis
                            }
                    }
                }

            _optimizationState.value = false
        }
    }
}