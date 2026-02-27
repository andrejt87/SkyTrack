package com.skytrack.feature.altitude

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.skytrack.core.design.theme.SkyTrackColors

data class AltitudeDataPoint(val timestampMs: Long, val meters: Double)

@Composable
fun AltitudeGraph(
    data: List<AltitudeDataPoint>,
    modifier: Modifier = Modifier
) {
    if (data.size < 2) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val maxAlt = data.maxOf { it.meters }.coerceAtLeast(1.0)
        val minAlt = data.minOf { it.meters }
        val timeRange = (data.last().timestampMs - data.first().timestampMs).coerceAtLeast(1L)

        val path = Path()
        data.forEachIndexed { index, point ->
            val x = ((point.timestampMs - data.first().timestampMs).toFloat() / timeRange) * size.width
            val y = size.height - ((point.meters - minAlt).toFloat() / (maxAlt - minAlt).toFloat()) * size.height

            if (index == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = SkyTrackColors.Accent,
            style = Stroke(width = 2f)
        )

        // Draw current position dot
        val lastPoint = data.last()
        val lastX = size.width
        val lastY = size.height - ((lastPoint.meters - minAlt).toFloat() / (maxAlt - minAlt).toFloat()) * size.height
        drawCircle(
            color = SkyTrackColors.Accent,
            radius = 6f,
            center = Offset(lastX, lastY)
        )
    }
}
