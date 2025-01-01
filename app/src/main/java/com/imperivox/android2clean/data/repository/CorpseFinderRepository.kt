package com.imperivox.android2clean.data.repository

import android.content.Context
import android.content.pm.PackageManager
import com.imperivox.android2clean.data.model.AppCorpse
import com.imperivox.android2clean.data.model.CorpseFile
import com.imperivox.android2clean.data.model.CorpseType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class CorpseFinderRepository(private val context: Context) {
    private val knownPaths = listOf(
        "/data/data",
        "/sdcard/Android/data",
        "/sdcard/Android/obb",
        "/sdcard/Android/media"
    )

    fun findCorpses(): Flow<List<AppCorpse>> = flow {
        val installedPackages = context.packageManager
            .getInstalledApplications(PackageManager.GET_META_DATA)
            .map { it.packageName }
            .toSet()

        val corpses = mutableListOf<AppCorpse>()

        knownPaths.forEach { basePath ->
            File(basePath).listFiles()?.forEach { dir ->
                val packageName = dir.name
                if (!installedPackages.contains(packageName)) {
                    val files = scanDirectory(dir)
                    if (files.isNotEmpty()) {
                        corpses.add(AppCorpse(
                            packageName = packageName,
                            files = files,
                            totalSize = files.sumOf { it.size },
                            lastModified = dir.lastModified()
                        ))
                    }
                }
            }
        }

        emit(corpses)
    }.flowOn(Dispatchers.IO)

    private fun scanDirectory(dir: File): List<CorpseFile> {
        val files = mutableListOf<CorpseFile>()
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                files.addAll(scanDirectory(file))
            } else {
                files.add(CorpseFile(
                    path = file.absolutePath,
                    size = file.length(),
                    type = determineFileType(file)
                ))
            }
        }
        return files
    }

    private fun determineFileType(file: File): CorpseType = when {
        file.path.contains("/cache/") -> CorpseType.CACHE
        file.path.contains("/databases/") -> CorpseType.DATABASE
        file.path.contains("/shared_prefs/") -> CorpseType.SHARED_PREFS
        file.path.contains("/files/") -> CorpseType.EXTERNAL_FILES
        else -> CorpseType.OTHER
    }

    suspend fun deleteCorpses(corpses: List<AppCorpse>): Boolean {
        return try {
            corpses.forEach { corpse ->
                corpse.files
                    .filter { it.isSelected }
                    .forEach { file ->
                        File(file.path).delete()
                    }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}