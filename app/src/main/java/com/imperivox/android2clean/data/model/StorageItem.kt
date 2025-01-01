package com.imperivox.android2clean.data.model

data class StorageItem(
    val name: String,
    val path: String,
    val size: Long,
    val type: StorageType
)

enum class StorageType {
    APP,
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT,
    OTHER
}