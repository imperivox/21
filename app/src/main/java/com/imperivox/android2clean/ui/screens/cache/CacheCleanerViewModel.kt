package com.imperivox.android2clean.ui.screens.cache

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imperivox.android2clean.data.model.JunkFile
import com.imperivox.android2clean.data.repository.CleanerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CacheCleanerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CleanerRepository(application)
    
    private val _junkFiles = MutableStateFlow<List<JunkFile>>(emptyList())
    val junkFiles: StateFlow<List<JunkFile>> = _junkFiles
    
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning
    
    private val _isCleaning = MutableStateFlow(false)
    val isCleaning: StateFlow<Boolean> = _isCleaning
    
    fun scanJunkFiles() {
        viewModelScope.launch {
            _isScanning.value = true
            repository.scanJunkFiles()
                .collect { files ->
                    _junkFiles.value = files
                    _isScanning.value = false
                }
        }
    }
    
    fun cleanSelectedFiles() {
        viewModelScope.launch {
            _isCleaning.value = true
            val selectedFiles = _junkFiles.value.filter { it.isSelected }
            repository.cleanJunkFiles(selectedFiles)
            scanJunkFiles() // Refresh the list after cleaning
            _isCleaning.value = false
        }
    }
}