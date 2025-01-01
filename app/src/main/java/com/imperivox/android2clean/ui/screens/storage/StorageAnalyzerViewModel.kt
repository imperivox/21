package com.imperivox.android2clean.ui.screens.storage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imperivox.android2clean.data.model.StorageItem
import com.imperivox.android2clean.data.repository.StorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StorageAnalyzerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StorageRepository(application)

    private val _storageItems = MutableStateFlow<List<StorageItem>>(emptyList())
    val storageItems: StateFlow<List<StorageItem>> = _storageItems

    private val _storageStats = MutableStateFlow<StorageRepository.StorageStats?>(null)
    val storageStats: StateFlow<StorageRepository.StorageStats?> = _storageStats

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing

    fun analyzeStorage() {
        viewModelScope.launch {
            _isAnalyzing.value = true

            // Get storage stats
            repository.getStorageStats()
                .collect { stats ->
                    _storageStats.value = stats
                }

            // Get storage items
            repository.analyzeStorage()
                .collect { items ->
                    _storageItems.value = items
                    _isAnalyzing.value = false
                }
        }
    }
}