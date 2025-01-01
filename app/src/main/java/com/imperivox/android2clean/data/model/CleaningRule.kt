package com.imperivox.android2clean.data.model

data class CleaningRule(
    val name: String,
    val description: String,
    val patterns: List<String>,
    val type: SystemFileType,
    val isEnabled: Boolean = true
)