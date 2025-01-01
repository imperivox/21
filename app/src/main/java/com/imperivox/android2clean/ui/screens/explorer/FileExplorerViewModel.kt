package com.imperivox.android2clean.ui.screens.explorer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imperivox.android2clean.data.model.FileItem
import com.imperivox.android2clean.data.repository.FileExplorerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class FileExplorerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FileExplorerRepository(application)

    private val _currentPath = MutableStateFlow(getInitialPath())
    val currentPath: StateFlow<String> = _currentPath

    private val _files = MutableStateFlow<List<FileItem>>(emptyList())
    val files: StateFlow<List<FileItem>> = _files

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    // Dialog states
    private val _showRenameDialog = MutableStateFlow<FileItem?>(null)
    val showRenameDialog: StateFlow<FileItem?> = _showRenameDialog

    private val _showCopyDialog = MutableStateFlow<FileItem?>(null)
    val showCopyDialog: StateFlow<FileItem?> = _showCopyDialog

    private val _showMoveDialog = MutableStateFlow<FileItem?>(null)
    val showMoveDialog: StateFlow<FileItem?> = _showMoveDialog

    init {
        loadCurrentDirectory()
    }

    private fun getInitialPath(): String {
        return getApplication<Application>().getExternalFilesDir(null)?.parentFile?.absolutePath
            ?: "/storage/emulated/0"
    }

    fun navigateTo(path: String) {
        _currentPath.value = path
        loadCurrentDirectory()
    }

    fun navigateUp() {
        val currentDir = File(_currentPath.value)
        currentDir.parentFile?.let { parent ->
            navigateTo(parent.absolutePath)
        }
    }

    fun refresh() {
        loadCurrentDirectory()
    }

    private fun loadCurrentDirectory() {
        viewModelScope.launch {
            repository.listFiles(_currentPath.value)
                .collect { fileList ->
                    _files.value = fileList
                }
        }
    }

    fun searchFiles(query: String, searchContent: Boolean) {
        viewModelScope.launch {
            _isSearching.value = true
            repository.searchFiles(query, _currentPath.value, searchContent)
                .collect { results ->
                    _files.value = results
                    _isSearching.value = false
                }
        }
    }

    // File operations
    fun initiateRename(file: FileItem) {
        _showRenameDialog.value = file
    }

    fun initiateCopy(file: FileItem) {
        _showCopyDialog.value = file
    }

    fun initiateMove(file: FileItem) {
        _showMoveDialog.value = file
    }

    fun cancelFileOperation() {
        _showRenameDialog.value = null
        _showCopyDialog.value = null
        _showMoveDialog.value = null
    }

    fun performRename(file: FileItem, newName: String) {
        viewModelScope.launch {
            val newPath = File(file.path).parentFile?.absolutePath + File.separator + newName
            if (repository.moveFile(file.path, newPath)) {
                refresh()
            }
            _showRenameDialog.value = null
        }
    }

    fun performCopy(file: FileItem, targetPath: String) {
        viewModelScope.launch {
            if (repository.copyFile(file.path, targetPath)) {
                refresh()
            }
            _showCopyDialog.value = null
        }
    }

    fun performMove(file: FileItem, targetPath: String) {
        viewModelScope.launch {
            if (repository.moveFile(file.path, targetPath)) {
                refresh()
            }
            _showMoveDialog.value = null
        }
    }

    fun deleteFile(file: FileItem) {
        viewModelScope.launch {
            repository.deleteFile(file.path)
            refresh()
        }
    }
}