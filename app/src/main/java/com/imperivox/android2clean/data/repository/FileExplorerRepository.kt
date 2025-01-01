package com.imperivox.android2clean.data.repository

import android.content.Context
import android.webkit.MimeTypeMap
import com.imperivox.android2clean.data.model.FileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

class FileExplorerRepository(private val context: Context) {
    fun listFiles(path: String): Flow<List<FileItem>> = flow {
        val directory = File(path)
        val files = directory.listFiles()?.map { file ->
            FileItem(
                name = file.name,
                path = file.absolutePath,
                size = file.length(),
                isDirectory = file.isDirectory,
                lastModified = Date(file.lastModified()),
                mimeType = getMimeType(file.absolutePath),
                isHidden = file.isHidden
            )
        }?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
        emit(files ?: emptyList())
    }.flowOn(Dispatchers.IO)

    fun searchFiles(
        query: String,
        searchPath: String,
        searchContent: Boolean = false
    ): Flow<List<FileItem>> = flow {
        val results = mutableListOf<FileItem>()
        searchRecursively(File(searchPath), query, searchContent, results)
        emit(results)
    }.flowOn(Dispatchers.IO)

    private suspend fun searchRecursively(
        directory: File,
        query: String,
        searchContent: Boolean,
        results: MutableList<FileItem>
    ) = withContext(Dispatchers.IO) {
        directory.listFiles()?.forEach { file ->
            if (file.name.contains(query, ignoreCase = true) ||
                (searchContent && file.isFile && containsContent(file, query))) {
                results.add(
                    FileItem(
                        name = file.name,
                        path = file.absolutePath,
                        size = file.length(),
                        isDirectory = file.isDirectory,
                        lastModified = Date(file.lastModified()),
                        mimeType = getMimeType(file.absolutePath),
                        isHidden = file.isHidden
                    )
                )
            }
            if (file.isDirectory) {
                searchRecursively(file, query, searchContent, results)
            }
        }
    }

    private fun containsContent(file: File, query: String): Boolean {
        return try {
            file.readText().contains(query, ignoreCase = true)
        } catch (e: Exception) {
            false
        }
    }

    private fun getMimeType(path: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(path)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }

    suspend fun copyFile(source: String, destination: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                File(source).copyTo(File(destination), overwrite = true)
                true
            } catch (e: Exception) {
                false
            }
        }

    suspend fun moveFile(source: String, destination: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                File(source).renameTo(File(destination))
            } catch (e: Exception) {
                false
            }
        }

    suspend fun deleteFile(path: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                File(path).deleteRecursively()
            } catch (e: Exception) {
                false
            }
        }

    suspend fun createDirectory(path: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                File(path).mkdirs()
            } catch (e: Exception) {
                false
            }
        }
}