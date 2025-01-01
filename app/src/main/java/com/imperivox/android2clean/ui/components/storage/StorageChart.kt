package com.imperivox.android2clean.ui.components.storage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.imperivox.android2clean.data.model.StorageType

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
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)
        var startAngle = 0f
        
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
            startAngle += sweepAngle
        }
    }
}