package com.imperivox.android2clean.data.model

data class SystemFile(
    val path: String,
    val size: Long,
    val type: SystemFileType,
    val isSelected: Boolean = true
)

enum class SystemFileType {
    TEMP_FILE,
    LOG_FILE,
    EMPTY_FOLDER,
    THUMBNAIL_CACHE,
    OLD_APK,
    RESIDUAL_APP_DATA
}