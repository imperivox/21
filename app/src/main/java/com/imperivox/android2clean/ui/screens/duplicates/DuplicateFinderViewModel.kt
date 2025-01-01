package com.imperivox.android2clean.ui.screens.duplicates

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imperivox.android2clean.data.model.DuplicateFile
import com.imperivox.android2clean.data.model.DuplicateGroup
import com.imperivox.android2clean.data.repository.DuplicateFinderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DuplicateFinderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DuplicateFinderRepository(application)

    private val _duplicateGroups = MutableStateFlow<List<DuplicateGroup>>(emptyList())
    val duplicateGroups: StateFlow<List<DuplicateGroup>> = _duplicateGroups

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val _quickScan = MutableStateFlow(true)
    val quickScan: StateFlow<Boolean> = _quickScan

    fun setQuickScan(enabled: Boolean) {
        _quickScan.value = enabled
    }

    fun startScan() {
        viewModelScope.launch {
            _isScanning.value = true
            repository.findDuplicates(quickScan = _quickScan.value)
                .collect { groups ->
                    _duplicateGroups.value = groups
                    _isScanning.value = false
                }
        }
    }

    fun toggleFileSelection(path: String, selected: Boolean) {
        val updatedGroups = _duplicateGroups.value.map { group ->
            group.copy(
                files = group.files.map { file ->
                    if (file.path == path) file.copy(isSelected = selected) else file
                }
            )
        }
        _duplicateGroups.value = updatedGroups
    }

    fun deleteSelectedDuplicates() {
        viewModelScope.launch {
            val selectedFiles = _duplicateGroups.value
                .flatMap { it.files }
                .filter { it.isSelected }

            if (repository.deleteDuplicates(selectedFiles)) {
                startScan() // Refresh the list after deletion
            }
        }
    }
}