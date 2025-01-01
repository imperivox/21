package com.imperivox.android2clean.data.model

data class DuplicateFile(
    val path: String,
    val size: Long,
    val hash: String,
    val isSelected: Boolean = false
)

data class DuplicateGroup(
    val hash: String,
    val size: Long,
    val files: List<DuplicateFile>
)