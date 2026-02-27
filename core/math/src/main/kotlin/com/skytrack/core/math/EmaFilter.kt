package com.skytrack.core.math

/**
 * Exponential Moving Average filter for smoothing sensor data.
 * Default alpha = 0.15 as specified in FA-001.
 */
class EmaFilter(
    private val alpha: Float = 0.15f
) {
    private var previousValue: Float? = null

    init {
        require(alpha in 0f..1f) { "Alpha must be between 0 and 1, got $alpha" }
    }

    fun apply(rawValue: Float): Float {
        val prev = previousValue
        val smoothed = if (prev == null) {
            rawValue
        } else {
            alpha * rawValue + (1 - alpha) * prev
        }
        previousValue = smoothed
        return smoothed
    }

    fun reset() {
        previousValue = null
    }

    val currentValue: Float?
        get() = previousValue
}
