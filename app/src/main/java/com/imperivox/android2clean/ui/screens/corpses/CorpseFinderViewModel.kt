package com.imperivox.android2clean.ui.screens.corpses

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imperivox.android2clean.data.model.AppCorpse
import com.imperivox.android2clean.data.repository.CorpseFinderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CorpseFinderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CorpseFinderRepository(application)

    private val _corpses = MutableStateFlow<List<AppCorpse>>(emptyList())
    val corpses: StateFlow<List<AppCorpse>> = _corpses

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    fun startScan() {
        viewModelScope.launch {
            _isScanning.value = true
            repository.findCorpses()
                .collect { foundCorpses ->
                    _corpses.value = foundCorpses
                    _isScanning.value = false
                }
        }
    }

    fun toggleFileSelection(packageName: String, filePath: String, selected: Boolean) {
        _corpses.value = _corpses.value.map { corpse ->
            if (corpse.packageName == packageName) {
                corpse.copy(
                    files = corpse.files.map { file ->
                        if (file.path == filePath) file.copy(isSelected = selected)
                        else file
                    }
                )
            } else corpse
        }
    }

    fun deleteSelectedCorpses() {
        viewModelScope.launch {
            val selectedCorpses = _corpses.value.filter { corpse ->
                corpse.files.any { it.isSelected }
            }
            if (repository.deleteCorpses(selectedCorpses)) {
                startScan() // Refresh the list after deletion
            }
        }
    }
}