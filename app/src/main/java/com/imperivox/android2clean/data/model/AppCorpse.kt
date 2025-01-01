package com.imperivox.android2clean.data.model

data class AppCorpse(
    val packageName: String,
    val files: List<CorpseFile>,
    val totalSize: Long,
    val lastModified: Long
)

data class CorpseFile(
    val path: String,
    val size: Long,
    val type: CorpseType,
    val isSelected: Boolean = true
)

enum class CorpseType {
    CACHE,
    DATA,
    SHARED_PREFS,
    DATABASE,
    EXTERNAL_FILES,
    OTHER
}