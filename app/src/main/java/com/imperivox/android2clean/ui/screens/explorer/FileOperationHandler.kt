package com.imperivox.android2clean.ui.screens.explorer

import com.imperivox.android2clean.data.model.FileItem
import com.imperivox.android2clean.data.repository.FileExplorerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class FileOperationsHandler(private val repository: FileExplorerRepository) {
    private val _showRenameDialog = MutableStateFlow<FileItem?>(null)
    val showRenameDialog: StateFlow<FileItem?> = _showRenameDialog

    private val _showCopyDialog = MutableStateFlow<FileItem?>(null)
    val showCopyDialog: StateFlow<FileItem?> = _showCopyDialog

    private val _showMoveDialog = MutableStateFlow<FileItem?>(null)
    val showMoveDialog: StateFlow<FileItem?> = _showMoveDialog

    suspend fun copyFile(file: FileItem, targetPath: String): Boolean {
        return try {
            val targetFile = File(targetPath)
            if (!targetFile.parentFile?.exists()!!) {
                targetFile.parentFile?.mkdirs()
            }
            repository.copyFile(file.path, targetPath)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun moveFile(file: FileItem, targetPath: String): Boolean {
        return try {
            val targetFile = File(targetPath)
            if (!targetFile.parentFile?.exists()!!) {
                targetFile.parentFile?.mkdirs()
            }
            repository.moveFile(file.path, targetPath)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun renameFile(file: FileItem, newName: String): Boolean {
        return try {
            val newPath = File(file.path).parentFile?.absolutePath + File.separator + newName
            repository.moveFile(file.path, newPath)
        } catch (e: Exception) {
            false
        }
    }

    fun showRenameDialog(file: FileItem) {
        _showRenameDialog.value = file
    }

    fun hideRenameDialog() {
        _showRenameDialog.value = null
    }

    fun showCopyDialog(file: FileItem) {
        _showCopyDialog.value = file
    }

    fun hideCopyDialog() {
        _showCopyDialog.value = null
    }

    fun showMoveDialog(file: FileItem) {
        _showMoveDialog.value = file
    }

    fun hideMoveDialog() {
        _showMoveDialog.value = null
    }
}