package com.skytrack.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skytrack.app.ui.theme.*

enum class GpsStrength(val bars: Int, val color: Color, val label: String) {
    EXCELLENT(4, SpeedGreen, "GPS"),
    GOOD(3, Amber, "GPS"),
    FAIR(2, WarningLight, "GPS"),
    POOR(1, Error, "GPS"),
    NONE(0, TextTertiary, "GPS");

    companion object {
        fun fromAccuracy(accuracyM: Float): GpsStrength = when {
            accuracyM <= 0f -> NONE
            accuracyM < 5f -> EXCELLENT
            accuracyM < 15f -> GOOD
            accuracyM < 30f -> FAIR
            else -> POOR
        }
    }
}

@Composable
fun GpsSignalIndicator(
    accuracyM: Float,
    modifier: Modifier = Modifier
) {
    val strength = GpsStrength.fromAccuracy(accuracyM)
    val totalBars = 4
    val barWidth = 4.dp
    val barSpacing = 2.dp
    val maxBarHeight = 16.dp

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Canvas(
            modifier = Modifier
                .width((barWidth + barSpacing) * totalBars)
                .height(maxBarHeight)
        ) {
            val bw = barWidth.toPx()
            val bs = barSpacing.toPx()
            val mh = maxBarHeight.toPx()

            for (i in 0 until totalBars) {
                val barHeight = mh * (i + 1) / totalBars
                val active = i < strength.bars
                val color = if (active) strength.color else TextTertiary.copy(alpha = 0.3f)
                drawRect(
                    color = color,
                    topLeft = Offset(
                        x = i * (bw + bs),
                        y = mh - barHeight
                    ),
                    size = Size(bw, barHeight)
                )
            }
        }
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = if (accuracyM > 0f) "${accuracyM.toInt()}m" else "–",
            style = MaterialTheme.typography.labelSmall.copy(
                color = strength.color,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.alignByBaseline()
        )
    }
}
