package com.imperivox.android2clean.data.repository

import android.content.Context
import android.os.Environment
import com.imperivox.android2clean.data.model.DuplicateFile
import com.imperivox.android2clean.data.model.DuplicateGroup
import com.imperivox.android2clean.utils.FileHasher
import com.imperivox.android2clean.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

class DuplicateFinderRepository(private val context: Context) {
    fun findDuplicates(
        scanPath: String = Environment.getExternalStorageDirectory().path,
        quickScan: Boolean = true
    ): Flow<Result<List<DuplicateGroup>>> = flow {
        emit(Result.Loading)

        try {
            val fileMap = mutableMapOf<String, MutableList<DuplicateFile>>()
            val sizeGroups = mutableMapOf<Long, MutableList<File>>()

            // First pass: Group by size using coroutines
            withContext(Dispatchers.IO) {
                File(scanPath).walk()
                    .filter { it.isFile }
                    .forEach { file ->
                        sizeGroups.getOrPut(file.length()) { mutableListOf() }.add(file)
                    }
            }

            // Second pass: Hash files in same-size groups
            withContext(Dispatchers.IO) {
                sizeGroups.filter { it.value.size > 1 }.forEach { (size, files) ->
                    files.forEach { file ->
                        val hash = if (quickScan) {
                            quickHash(file)
                        } else {
                            fullHash(file)
                        }

                        fileMap.getOrPut(hash) { mutableListOf() }.add(
                            DuplicateFile(
                                path = file.absolutePath,
                                size = size,
                                hash = hash
                            )
                        )
                    }
                }
            }

            // Create duplicate groups
            val duplicateGroups = fileMap.filter { it.value.size > 1 }
                .map { (hash, files) ->
                    DuplicateGroup(
                        hash = hash,
                        size = files.first().size,
                        files = files
                    )
                }
                .sortedByDescending { it.size * it.files.size }

            emit(Result.Success(duplicateGroups))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun quickHash(file: File): String = withContext(Dispatchers.IO) {
        val buffer = ByteArray(8192)
        file.inputStream().use { input ->
            val bytesRead = input.read(buffer)
            if (bytesRead > 0) {
                MessageDigest.getInstance("MD5").run {
                    update(buffer, 0, bytesRead)
                    digest().joinToString("") { "%02x".format(it) }
                }
            } else {
                file.length().toString()
            }
        }
    }

    private suspend fun fullHash(file: File): String = withContext(Dispatchers.IO) {
        val md = MessageDigest.getInstance("MD5")
        val buffer = ByteArray(8192)

        file.inputStream().use { input ->
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                md.update(buffer, 0, bytesRead)
            }
        }

        md.digest().joinToString("") { "%02x".format(it) }
    }

    suspend fun deleteDuplicates(files: List<DuplicateFile>): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            files.forEach { file ->
                File(file.path).delete()
            }
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}