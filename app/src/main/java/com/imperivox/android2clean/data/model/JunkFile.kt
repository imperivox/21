package com.imperivox.android2clean.data.model

data class JunkFile(
    val path: String,
    val size: Long,
    val type: JunkType,
    val isSelected: Boolean = true
)

enum class JunkType {
    CACHE,
    TEMP,
    RESIDUAL,
    LOG,
    EMPTY_FOLDER
}