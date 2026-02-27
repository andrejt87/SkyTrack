package com.skytrack.core.math

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EmaFilterTest {

    @Test
    fun `first value passes through unfiltered`() {
        val ema = EmaFilter(alpha = 0.15f)
        val result = ema.apply(50f)
        assertThat(result).isEqualTo(50f)
    }

    @Test
    fun `subsequent values are smoothed`() {
        val ema = EmaFilter(alpha = 0.15f)
        ema.apply(50f)
        val result = ema.apply(100f)
        // Expected: 0.15 * 100 + 0.85 * 50 = 15 + 42.5 = 57.5
        assertThat(result).isWithin(0.01f).of(57.5f)
    }

    @Test
    fun `reset clears state`() {
        val ema = EmaFilter(alpha = 0.15f)
        ema.apply(50f)
        ema.apply(100f)
        ema.reset()
        val result = ema.apply(30f)
        assertThat(result).isEqualTo(30f)
    }
}
