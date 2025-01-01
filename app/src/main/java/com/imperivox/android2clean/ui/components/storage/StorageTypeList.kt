package com.imperivox.android2clean.ui.components.storage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.imperivox.android2clean.data.model.StorageItem
import com.imperivox.android2clean.data.model.StorageType

@Composable
fun StorageTypeList(storageItems: List<StorageItem>) {
    val groupedItems = storageItems.groupBy { it.type }

    Column {
        StorageType.values().forEach { type ->
            val typeItems = groupedItems[type] ?: emptyList()
            val totalSize = typeItems.sumOf { it.size }

            if (totalSize > 0) {
                ListItem(
                    headlineContent = { Text(type.name) },
                    supportingContent = { Text("${totalSize / 1024 / 1024} MB") }
                )
            }
        }
    }
}