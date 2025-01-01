package com.imperivox.android2clean.data.model

import java.util.Date

data class FileItem(
    val name: String,
    val path: String,
    val size: Long,
    val isDirectory: Boolean,
    val lastModified: Date,
    val mimeType: String?,
    val isHidden: Boolean
)