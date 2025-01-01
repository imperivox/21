package com.imperivox.android2clean.utils

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    fun formatFileSize(size: Long): String = when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${size / 1024} KB"
        size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
        else -> "${size / (1024 * 1024 * 1024)} GB"
    }

    fun formatDate(date: Date): String {
        return SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(date)
    }

    fun isValidDirectory(path: String): Boolean {
        return try {
            val file = File(path)
            file.exists() && file.isDirectory && file.canRead()
        } catch (e: Exception) {
            false
        }
    }

    fun searchInFile(file: File, query: String): Boolean {
        return try {
            if (!file.isFile || !file.canRead()) return false
            // Only search in text files to avoid binary file issues
            if (!isTextFile(file)) return false

            file.bufferedReader().use { reader ->
                reader.lineSequence().any { line ->
                    line.contains(query, ignoreCase = true)
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun isTextFile(file: File): Boolean {
        val textExtensions = setOf(
            "txt", "log", "xml", "json", "md", "csv",
            "properties", "ini", "conf", "yaml", "yml"
        )
        return file.extension.lowercase() in textExtensions
    }
}