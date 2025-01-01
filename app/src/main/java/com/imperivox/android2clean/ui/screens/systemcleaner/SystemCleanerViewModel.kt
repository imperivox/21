package com.imperivox.android2clean.ui.screens.systemcleaner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imperivox.android2clean.data.model.CleaningRule
import com.imperivox.android2clean.data.model.SystemFile
import com.imperivox.android2clean.data.repository.SystemCleanerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SystemCleanerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SystemCleanerRepository(application)

    private val _rules = MutableStateFlow<List<CleaningRule>>(emptyList())
    val rules: StateFlow<List<CleaningRule>> = _rules

    private val _systemFiles = MutableStateFlow<List<SystemFile>>(emptyList())
    val systemFiles: StateFlow<List<SystemFile>> = _systemFiles

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    init {
        _rules.value = repository.getRules()
    }

    fun toggleRule(rule: CleaningRule, enabled: Boolean) {
        _rules.value = _rules.value.map {
            if (it.name == rule.name) it.copy(isEnabled = enabled) else it
        }
    }

    fun startScan() {
        viewModelScope.launch {
            _isScanning.value = true
            repository.scanSystem(_rules.value)
                .collect { files ->
                    _systemFiles.value = files
                    _isScanning.value = false
                }
        }
    }

    fun toggleFileSelection(path: String, selected: Boolean) {
        _systemFiles.value = _systemFiles.value.map {
            if (it.path == path) it.copy(isSelected = selected) else it
        }
    }

    fun cleanSelectedFiles() {
        viewModelScope.launch {
            val selectedFiles = _systemFiles.value.filter { it.isSelected }
            if (repository.cleanFiles(selectedFiles)) {
                startScan() // Refresh the list after cleaning
            }
        }
    }
}