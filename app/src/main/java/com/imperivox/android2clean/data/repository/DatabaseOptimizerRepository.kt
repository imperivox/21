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
                val initialSize = dbFile.length()
                val db = SQLiteDatabase.openDatabase(
                    dbFile.path,
                    null,
                    SQLiteDatabase.OPEN_READWRITE
                )
                db.use {
                    // Run PRAGMA commands for optimization
                    it.execSQL("PRAGMA auto_vacuum = FULL")
                    it.execSQL("PRAGMA optimize")
                    it.execSQL("VACUUM")

                    // Check integrity
                    val cursor = it.rawQuery("PRAGMA integrity_check", null)
                    cursor.use { c ->
                        if (c.moveToFirst() && c.getString(0) == "ok") {
                            val finalSize = dbFile.length()
                            val savedSpace = initialSize - finalSize
                            emit(OptimizationResult.Success(
                                dbFile.name,
                                savedSpace,
                                "Database optimized successfully"
                            ))
                        } else {
                            emit(OptimizationResult.Error(
                                dbFile.name,
                                "Database integrity check failed"
                            ))
                        }
                    }
                }
            } catch (e: Exception) {
                emit(OptimizationResult.Error(dbFile.name, e.message ?: "Unknown error"))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun analyzeDatabase(path: String): Flow<DatabaseAnalysis> = flow {
        try {
            val db = SQLiteDatabase.openDatabase(
                path,
                null,
                SQLiteDatabase.OPEN_READONLY
            )
            db.use {
                val tables = mutableListOf<TableInfo>()
                val cursor = it.rawQuery(
                    "SELECT name, sql FROM sqlite_master WHERE type='table'",
                    null
                )
                cursor.use { c ->
                    while (c.moveToNext()) {
                        val tableName = c.getString(0)
                        val tableSize = getTableSize(it, tableName)
                        tables.add(TableInfo(tableName, tableSize))
                    }
                }
                emit(DatabaseAnalysis(File(path).name, tables))
            }
        } catch (e: Exception) {
            emit(DatabaseAnalysis(File(path).name, emptyList(), e.message))
        }
    }.flowOn(Dispatchers.IO)

    private fun getTableSize(db: SQLiteDatabase, tableName: String): Long {
        val cursor = db.rawQuery("SELECT count(*) FROM $tableName", null)
        return cursor.use {
            if (it.moveToFirst()) it.getLong(0) else 0
        }
    }
}

sealed class OptimizationResult {
    data class Success(
        val databaseName: String,
        val spaceSaved: Long = 0,
        val message: String = ""
    ) : OptimizationResult()

    data class Error(
        val databaseName: String,
        val error: String
    ) : OptimizationResult()
}

data class DatabaseAnalysis(
    val databaseName: String,
    val tables: List<TableInfo>,
    val error: String? = null
)

data class TableInfo(
    val name: String,
    val rowCount: Long
)