package com.imperivox.android2clean.data.repository

import android.content.Context
import com.imperivox.android2clean.data.model.JunkFile
import com.imperivox.android2clean.data.scanner.JunkScanner
import kotlinx.coroutines.flow.Flow
import java.io.File

class CleanerRepository(context: Context) {
    private val junkScanner = JunkScanner(context)
    
    fun scanJunkFiles(): Flow<List<JunkFile>> = junkScanner.scanJunkFiles()
    
    suspend fun cleanJunkFiles(files: List<JunkFile>): Boolean {
        return try {
            files.forEach { junkFile ->
                File(junkFile.path).delete()
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}