package com.imperivox.android2clean.data.repository

import android.content.Context
import android.os.Environment
import com.imperivox.android2clean.data.model.DuplicateFile
import com.imperivox.android2clean.data.model.DuplicateGroup
import com.imperivox.android2clean.utils.FileHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class DuplicateFinderRepository(private val context: Context) {
    fun findDuplicates(
        scanPath: String = Environment.getExternalStorageDirectory().path,
        quickScan: Boolean = true
    ): Flow<List<DuplicateGroup>> = flow {
        val fileMap = mutableMapOf<String, MutableList<DuplicateFile>>()

        // First pass: Group by size
        val sizeGroups = mutableMapOf<Long, MutableList<File>>()
        File(scanPath).walk()
            .filter { it.isFile }
            .forEach { file ->
                sizeGroups.getOrPut(file.length()) { mutableListOf() }.add(file)
            }

        // Second pass: Hash files in same-size groups
        sizeGroups.filter { it.value.size > 1 }.forEach { (size, files) ->
            files.forEach { file ->
                val hash = if (quickScan) {
                    FileHasher.quickHash(file)
                } else {
                    FileHasher.hashFile(file)
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

        emit(duplicateGroups)
    }.flowOn(Dispatchers.IO)

    suspend fun deleteDuplicates(files: List<DuplicateFile>): Boolean {
        return try {
            files.forEach { file ->
                File(file.path).delete()
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}