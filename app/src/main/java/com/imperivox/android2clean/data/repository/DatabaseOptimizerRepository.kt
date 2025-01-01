package com.imperivox.android2clean.data.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class DatabaseOptimizerRepository(private val context: Context) {
    fun optimizeDatabases(): Flow<OptimizationResult> = flow {
        val databaseDir = context.applicationInfo.dataDir + "/databases"
        val dbFiles = File(databaseDir).listFiles { file ->
            file.extension == "db" || file.name.endsWith(".sqlite")
        }

        dbFiles?.forEach { dbFile ->
            try {
                val db = SQLiteDatabase.openDatabase(
                    dbFile.path,
                    null,
                    SQLiteDatabase.OPEN_READWRITE
                )
                db.use {
                    it.execSQL("VACUUM")
                    emit(OptimizationResult.Success(dbFile.name))
                }
            } catch (e: Exception) {
                emit(OptimizationResult.Error(dbFile.name, e.message ?: "Unknown error"))
            }
        }
    }.flowOn(Dispatchers.IO)
}

sealed class OptimizationResult {
    data class Success(val databaseName: String) : OptimizationResult()
    data class Error(val databaseName: String, val error: String) : OptimizationResult()
}
