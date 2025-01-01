package com.imperivox.android2clean.ui.components.systemcleaner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imperivox.android2clean.data.model.CleaningRule

@Composable
fun CleaningRuleItem(
    rule: CleaningRule,
    onRuleToggled: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = rule.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Switch(
                checked = rule.isEnabled,
                onCheckedChange = onRuleToggled
            )
        }
    }
}