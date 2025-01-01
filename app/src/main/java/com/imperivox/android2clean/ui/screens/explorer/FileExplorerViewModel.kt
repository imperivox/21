package com.imperivox.android2clean.ui.screens.explorer

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imperivox.android2clean.data.model.FileItem
import com.imperivox.android2clean.data.repository.FileExplorerRepository
import com.imperivox.android2clean.ui.utils.FileUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class FileExplorerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FileExplorerRepository(application)
    private val fileOperationsHandler = FileOperationsHandler(repository)

    private val _currentPath = MutableStateFlow(getInitialPath())
    val currentPath: StateFlow<String> = _currentPath

    private val _files = MutableStateFlow<List<FileItem>>(emptyList())
    val files: StateFlow<List<FileItem>> = _files

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val showRenameDialog: StateFlow<FileItem?> = fileOperationsHandler.showRenameDialog
    val showCopyDialog: StateFlow<FileItem?> = fileOperationsHandler.showCopyDialog
    val showMoveDialog: StateFlow<FileItem?> = fileOperationsHandler.showMoveDialog

    private fun getInitialPath(): String {
        return Environment.getExternalStorageDirectory().absolutePath
    }

    fun searchFiles(query: String, searchContent: Boolean) {
        if (query.isBlank()) return

        viewModelScope.launch {
            try {
                _isSearching.value = true
                _error.value = null

                repository.searchFiles(query, _currentPath.value, searchContent)
                    .collect { results ->
                        _files.value = results
                    }
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
                _files.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun navigateTo(path: String) {
        try {
            if (FileUtils.isValidDirectory(path)) {
                _currentPath.value = path
                loadCurrentDirectory()
            } else {
                _error.value = "Invalid directory"
            }
        } catch (e: Exception) {
            _error.value = "Navigation failed: ${e.message}"
        }
    }

    fun navigateUp() {
        try {
            val currentDir = File(_currentPath.value)
            currentDir.parentFile?.let { parent ->
                if (FileUtils.isValidDirectory(parent.absolutePath)) {
                    navigateTo(parent.absolutePath)
                }
            }
        } catch (e: Exception) {
            _error.value = "Navigation failed: ${e.message}"
        }
    }

    fun refresh() {
        loadCurrentDirectory()
    }

    private fun loadCurrentDirectory() {
        viewModelScope.launch {
            try {
                _error.value = null
                repository.listFiles(_currentPath.value)
                    .collect { fileList ->
                        _files.value = fileList
                    }
            } catch (e: Exception) {
                _error.value = "Failed to load directory: ${e.message}"
                _files.value = emptyList()
            }
        }
    }

    // File operations
    fun initiateCopy(file: FileItem) {
        fileOperationsHandler.showCopyDialog(file)
    }

    fun initiateMove(file: FileItem) {
        fileOperationsHandler.showMoveDialog(file)
    }

    fun initiateRename(file: FileItem) {
        fileOperationsHandler.showRenameDialog(file)
    }

    fun cancelFileOperation() {
        fileOperationsHandler.apply {
            hideCopyDialog()
            hideMoveDialog()
            hideRenameDialog()
        }
    }

    fun performCopy(file: FileItem, targetPath: String) {
        viewModelScope.launch {
            try {
                if (fileOperationsHandler.copyFile(file, targetPath)) {
                    refresh()
                } else {
                    _error.value = "Failed to copy file"
                }
            } catch (e: Exception) {
                _error.value = "Copy failed: ${e.message}"
            }
            cancelFileOperation()
        }
    }

    fun performMove(file: FileItem, targetPath: String) {
        viewModelScope.launch {
            try {
                if (fileOperationsHandler.moveFile(file, targetPath)) {
                    refresh()
                } else {
                    _error.value = "Failed to move file"
                }
            } catch (e: Exception) {
                _error.value = "Move failed: ${e.message}"
            }
            cancelFileOperation()
        }
    }

    fun performRename(file: FileItem, newName: String) {
        viewModelScope.launch {
            try {
                if (fileOperationsHandler.renameFile(file, newName)) {
                    refresh()
                } else {
                    _error.value = "Failed to rename file"
                }
            } catch (e: Exception) {
                _error.value = "Rename failed: ${e.message}"
            }
            cancelFileOperation()
        }
    }

    fun deleteFile(file: FileItem) {
        viewModelScope.launch {
            try {
                if (repository.deleteFile(file.path)) {
                    refresh()
                } else {
                    _error.value = "Failed to delete file"
                }
            } catch (e: Exception) {
                _error.value = "Delete failed: ${e.message}"
            }
        }
    }
}