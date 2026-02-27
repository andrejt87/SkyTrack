package com.skytrack.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.skytrack.app.ui.theme.TextPrimary

/**
 * An animated number counter that rolls from old value to new value.
 */
@Composable
fun AnimatedCounter(
    value: Int,
    modifier: Modifier = Modifier,
    suffix: String = "",
    prefix: String = "",
    style: TextStyle = MaterialTheme.typography.displayLarge.copy(color = TextPrimary)
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 700, easing = EaseInOutCubic),
        label = "counter"
    )
    Text(
        text = "$prefix$animatedValue$suffix",
        style = style,
        modifier = modifier
    )
}

/**
 * Float version – e.g. for showing "847.5 km/h"
 */
@Composable
fun AnimatedFloatCounter(
    value: Float,
    decimals: Int = 1,
    modifier: Modifier = Modifier,
    suffix: String = "",
    prefix: String = "",
    style: TextStyle = MaterialTheme.typography.headlineMedium.copy(color = TextPrimary)
) {
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 600, easing = EaseOutCubic),
        label = "floatCounter"
    )
    val formatted = "%.${decimals}f".format(animatedValue)
    Text(
        text = "$prefix$formatted$suffix",
        style = style,
        modifier = modifier
    )
}
