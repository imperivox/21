package com.imperivox.android2clean.ui.components.explorer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onSearch: (query: String, searchContent: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var searchContent by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        TextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search files...") },
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = searchContent,
                onCheckedChange = { searchContent = it }
            )
            Text("Search in file contents")

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onSearch(query, searchContent) },
                enabled = query.isNotBlank()
            ) {
                Text("Search")
            }
        }
    }
}