package com.imperivox.android2clean.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imperivox.android2clean.data.model.JunkFile
import com.imperivox.android2clean.data.repository.CleanerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CleanerRepository(application)

    private val _junkFiles = MutableStateFlow<List<JunkFile>>(emptyList())
    val junkFiles: StateFlow<List<JunkFile>> = _junkFiles

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    fun startJunkScan() {
        viewModelScope.launch {
            _isScanning.value = true
            repository.scanJunkFiles()
                .catch { /* Handle error */ }
                .collect { files ->
                    _junkFiles.value = files
                    _isScanning.value = false
                }
        }
    }

    fun cleanSelectedJunk() {
        viewModelScope.launch {
            val selectedFiles = _junkFiles.value.filter { it.isSelected }
            repository.cleanJunkFiles(selectedFiles)
            startJunkScan() // Refresh the list after cleaning
        }
    }
}