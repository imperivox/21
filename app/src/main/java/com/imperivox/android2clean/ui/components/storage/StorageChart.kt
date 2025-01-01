package com.imperivox.android2clean.ui.components.storage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.imperivox.android2clean.data.model.StorageType
import com.imperivox.android2clean.utils.FileUtils

@Composable
fun StorageChart(
    items: Map<StorageType, Long>,
    modifier: Modifier = Modifier
) {
    val total = items.values.sum().toFloat()
    val colors = mapOf(
        StorageType.APP to Color(0xFF2196F3),
        StorageType.IMAGE to Color(0xFF4CAF50),
        StorageType.VIDEO to Color(0xFFF44336),
        StorageType.AUDIO to Color(0xFFFF9800),
        StorageType.DOCUMENT to Color(0xFF9C27B0),
        StorageType.OTHER to Color(0xFF607D8B)
    )
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surface
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        ) {
            val radius = size.minDimension / 2
            val center = Offset(size.width / 2, size.height / 2)
            var startAngle = 0f

            // Draw chart background
            drawCircle(
                color = surfaceVariantColor,
                radius = radius,
                center = center
            )

            // Draw segments
            items.forEach { (type, size) ->
                val sweepAngle = (size / total) * 360f
                drawArc(
                    color = colors[type] ?: Color.Gray,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
                // Draw segment border
                drawArc(
                    color = surfaceColor,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    style = Stroke(width = 2f),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
                startAngle += sweepAngle
            }
        }

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { (type, size) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(colors[type] ?: Color.Gray)
                    )
                    Text(
                        text = type.name,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = FileUtils.formatFileSize(size),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}