package com.imperivox.android2clean.data.repository

import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.imperivox.android2clean.data.model.StorageItem
import com.imperivox.android2clean.data.model.StorageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class StorageRepository(private val context: Context) {
    fun analyzeStorage(): Flow<List<StorageItem>> = flow {
        val items = mutableListOf<StorageItem>()
        val externalDir = Environment.getExternalStorageDirectory()

        scanDirectory(externalDir, items)
        emit(items)
    }

    fun getStorageStats(): Flow<StorageStats> = flow {
        val stats = StatFs(Environment.getExternalStorageDirectory().path)
        emit(StorageStats(
            totalSpace = stats.totalBytes,
            freeSpace = stats.freeBytes,
            usedSpace = stats.totalBytes - stats.freeBytes
        ))
    }

    private fun scanDirectory(directory: File, items: MutableList<StorageItem>) {
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                scanDirectory(file, items)
            } else {
                val type = when (file.extension.lowercase()) {
                    in imageExtensions -> StorageType.IMAGE
                    in videoExtensions -> StorageType.VIDEO
                    in audioExtensions -> StorageType.AUDIO
                    in documentExtensions -> StorageType.DOCUMENT
                    else -> StorageType.OTHER
                }
                items.add(StorageItem(file.name, file.path, file.length(), type))
            }
        }
    }

    data class StorageStats(
        val totalSpace: Long,
        val freeSpace: Long,
        val usedSpace: Long
    )

    companion object {
        private val imageExtensions = setOf("jpg", "jpeg", "png", "gif")
        private val videoExtensions = setOf("mp4", "mkv", "avi")
        private val audioExtensions = setOf("mp3", "wav", "ogg")
        private val documentExtensions = setOf("pdf", "doc", "docx", "txt")
    }
}