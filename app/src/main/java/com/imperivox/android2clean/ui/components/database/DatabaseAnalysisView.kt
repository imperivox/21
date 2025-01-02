package com.imperivox.android2clean.ui.components.database

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imperivox.android2clean.data.repository.DatabaseAnalysis

@Composable
fun DatabaseAnalysisView(analysis: DatabaseAnalysis) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Database: ${analysis.databaseName}",
                style = MaterialTheme.typography.titleMedium
            )

            if (analysis.error != null) {
                Text(
                    text = "Error: ${analysis.error}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tables: ${analysis.tables.size}",
                    style = MaterialTheme.typography.bodyMedium
                )

                analysis.tables.forEach { table ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = table.name,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${table.rowCount} rows",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}