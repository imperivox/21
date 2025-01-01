package com.imperivox.android2clean.data.scanner

import android.content.Context
import android.os.Environment
import com.imperivox.android2clean.data.model.JunkFile
import com.imperivox.android2clean.data.model.JunkType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class JunkScanner(private val context: Context) {
    fun scanJunkFiles(): Flow<List<JunkFile>> = flow {
        val junkFiles = mutableListOf<JunkFile>()

        // Scan app cache
        scanAppCache(junkFiles)

        // Scan temp files
        scanTempFiles(junkFiles)

        // Scan residual files
        scanResidualFiles(junkFiles)

        emit(junkFiles)
    }

    private fun scanAppCache(results: MutableList<JunkFile>) {
        val cacheDir = context.cacheDir
        scanDirectory(cacheDir, JunkType.CACHE, results)
    }

    private fun scanTempFiles(results: MutableList<JunkFile>) {
        val tempDir = File(context.filesDir, "temp")
        if (tempDir.exists()) {
            scanDirectory(tempDir, JunkType.TEMP, results)
        }
    }

    private fun scanResidualFiles(results: MutableList<JunkFile>) {
        val externalDir = Environment.getExternalStorageDirectory()
        val androidDir = File(externalDir, "Android/data")
        if (androidDir.exists()) {
            scanDirectory(androidDir, JunkType.RESIDUAL, results)
        }
    }

    private fun scanDirectory(directory: File, type: JunkType, results: MutableList<JunkFile>) {
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                scanDirectory(file, type, results)
                if (file.listFiles()?.isEmpty() == true) {
                    results.add(JunkFile(file.path, 0, JunkType.EMPTY_FOLDER))
                }
            } else {
                results.add(JunkFile(file.path, file.length(), type))
            }
        }
    }
}