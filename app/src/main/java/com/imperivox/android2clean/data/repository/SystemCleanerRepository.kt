package com.imperivox.android2clean.data.repository

import android.content.Context
import android.os.Environment
import com.imperivox.android2clean.data.model.SystemFile
import com.imperivox.android2clean.data.model.SystemFileType
import com.imperivox.android2clean.data.model.CleaningRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class SystemCleanerRepository(private val context: Context) {
    private val defaultRules = listOf(
        CleaningRule(
            name = "Temporary Files",
            description = "Remove temporary files created by apps",
            patterns = listOf(".tmp", ".temp", "*.temp.*"),
            type = SystemFileType.TEMP_FILE
        ),
        CleaningRule(
            name = "Log Files",
            description = "Remove old log files",
            patterns = listOf("*.log", "*.trace"),
            type = SystemFileType.LOG_FILE
        ),
        CleaningRule(
            name = "Empty Folders",
            description = "Remove empty directories",
            patterns = listOf(""),
            type = SystemFileType.EMPTY_FOLDER
        ),
        CleaningRule(
            name = "Thumbnail Cache",
            description = "Remove thumbnail cache files",
            patterns = listOf(".thumbnails"),
            type = SystemFileType.THUMBNAIL_CACHE
        ),
        CleaningRule(
            name = "Old APK Files",
            description = "Remove downloaded APK files",
            patterns = listOf("*.apk"),
            type = SystemFileType.OLD_APK
        )
    )

    fun scanSystem(rules: List<CleaningRule> = defaultRules): Flow<List<SystemFile>> = flow {
        val results = mutableListOf<SystemFile>()
        val rootDir = Environment.getExternalStorageDirectory()

        rules.filter { it.isEnabled }.forEach { rule ->
            when (rule.type) {
                SystemFileType.EMPTY_FOLDER -> scanEmptyFolders(rootDir, results)
                else -> scanMatchingFiles(rootDir, rule, results)
            }
        }

        emit(results)
    }.flowOn(Dispatchers.IO)

    private fun scanEmptyFolders(dir: File, results: MutableList<SystemFile>) {
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                if (file.listFiles()?.isEmpty() == true) {
                    results.add(
                        SystemFile(
                            path = file.absolutePath,
                            size = 0L,
                            type = SystemFileType.EMPTY_FOLDER
                        )
                    )
                } else {
                    scanEmptyFolders(file, results)
                }
            }
        }
    }

    private fun scanMatchingFiles(dir: File, rule: CleaningRule, results: MutableList<SystemFile>) {
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                scanMatchingFiles(file, rule, results)
            } else if (rule.patterns.any { pattern ->
                    file.name.matches(pattern.replace("*", ".*").toRegex())
                }) {
                results.add(
                    SystemFile(
                        path = file.absolutePath,
                        size = file.length(),
                        type = rule.type
                    )
                )
            }
        }
    }

    suspend fun cleanFiles(files: List<SystemFile>): Boolean {
        return try {
            files.forEach { file ->
                File(file.path).delete()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getRules(): List<CleaningRule> = defaultRules
}