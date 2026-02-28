package com.skytrack.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skytrack.app.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * The prominent hero widget on the Dashboard showing:
 * - Route label (e.g. "FRA → JFK")
 * - Big animated progress percentage
 * - Arc progress bar with a flying airplane indicator
 */
@Composable
fun ProgressHero(
    departureIata: String,
    arrivalIata: String,
    progressPercent: Double,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercent.toFloat().coerceIn(0f, 100f),
        animationSpec = tween(durationMillis = 800, easing = EaseInOutCubic),
        label = "progress"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Arc + plane + percentage
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(240.dp)
        ) {
            ArcProgressIndicator(
                progress = animatedProgress / 100f,
                modifier = Modifier.fillMaxSize()
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedCounter(
                    value = animatedProgress.toInt(),
                    suffix = "%",
                    style = MaterialTheme.typography.displayLarge.copy(
                        color = ProgressGold,
                        fontWeight = FontWeight.Black
                    )
                )
                Text(
                    text = "COMPLETE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextTertiary,
                        letterSpacing = 3.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Linear progress bar with plane icon
        LinearProgressBar(
            progress = animatedProgress / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun ArcProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val sweepAngle = 240f * progress
    Canvas(modifier = modifier) {
        val strokeWidth = 12.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2f
        val topLeft = Offset(
            x = center.x - radius,
            y = center.y - radius
        )
        val arcSize = Size(radius * 2, radius * 2)
        val startAngle = 150f

        // Track (background arc)
        drawArc(
            color = ProgressTrack,
            startAngle = startAngle,
            sweepAngle = 240f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Progress arc
        if (sweepAngle > 0f) {
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(AmberDark, Amber, AmberLight),
                    center = center
                ),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Plane indicator dot at progress tip
        if (progress > 0.01f) {
            val angleRad = Math.toRadians((startAngle + sweepAngle).toDouble())
            val dotX = center.x + radius * cos(angleRad).toFloat()
            val dotY = center.y + radius * sin(angleRad).toFloat()
            drawCircle(
                color = AircraftWhite,
                radius = 8.dp.toPx(),
                center = Offset(dotX, dotY)
            )
        }
    }
}

@Composable
private fun LinearProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val boxWidth = maxWidth
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barHeight = 4.dp.toPx()
            val y = center.y - barHeight / 2

            // Track
            drawRoundRect(
                color = ProgressTrack,
                topLeft = Offset(0f, y),
                size = Size(size.width, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barHeight / 2)
            )

            // Fill
            val fillWidth = size.width * progress
            if (fillWidth > 0f) {
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(AmberDark, Amber, AmberLight)
                    ),
                    topLeft = Offset(0f, y),
                    size = Size(fillWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(barHeight / 2)
                )
            }
        }

        // Plane icon at progress position
        val planeX = (boxWidth * progress - 12.dp).coerceAtLeast(0.dp)
        Icon(
            imageVector = Icons.Default.Flight,
            contentDescription = null,
            tint = AircraftWhite,
            modifier = Modifier
                .size(20.dp)
                .offset(x = planeX, y = 2.dp)
        )
    }
}
