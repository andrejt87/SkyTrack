package com.skytrack.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.skytrack.domain.model.Acceleration
import com.skytrack.domain.repository.AccelerometerRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DetectTurbulenceUseCaseTest {

    private val accelerometerRepository: AccelerometerRepository = mockk()

    @Test
    fun `normal gravity returns NONE`() = runTest {
        // 9.81 m/s² is standard gravity -> deviation ~0
        every { accelerometerRepository.accelerationFlow() } returns flowOf(
            Acceleration(x = 0f, y = 0f, z = 9.81f)
        )
        val useCase = DetectTurbulenceUseCase(accelerometerRepository)

        val level = useCase.execute().first()

        assertThat(level).isEqualTo(TurbulenceLevel.NONE)
    }

    @Test
    fun `slight deviation returns LIGHT turbulence`() = runTest {
        // magnitude ~10.81 -> deviation ~1.0 (between 0.5 and 1.5)
        every { accelerometerRepository.accelerationFlow() } returns flowOf(
            Acceleration(x = 1.0f, y = 0f, z = 9.81f)
        )
        val useCase = DetectTurbulenceUseCase(accelerometerRepository)

        val level = useCase.execute().first()

        assertThat(level).isEqualTo(TurbulenceLevel.LIGHT)
    }

    @Test
    fun `moderate deviation returns MODERATE turbulence`() = runTest {
        // magnitude ~12.0 -> deviation ~2.2 (between 1.5 and 3.0)
        every { accelerometerRepository.accelerationFlow() } returns flowOf(
            Acceleration(x = 2.0f, y = 1.0f, z = 9.81f)
        )
        val useCase = DetectTurbulenceUseCase(accelerometerRepository)

        val level = useCase.execute().first()

        assertThat(level).isEqualTo(TurbulenceLevel.MODERATE)
    }

    @Test
    fun `severe deviation returns SEVERE turbulence`() = runTest {
        // magnitude ~15.0 -> deviation ~5.2 (> 3.0)
        every { accelerometerRepository.accelerationFlow() } returns flowOf(
            Acceleration(x = 5.0f, y = 5.0f, z = 9.81f)
        )
        val useCase = DetectTurbulenceUseCase(accelerometerRepository)

        val level = useCase.execute().first()

        assertThat(level).isEqualTo(TurbulenceLevel.SEVERE)
    }

    @Test
    fun `zero acceleration returns SEVERE`() = runTest {
        // magnitude = 0 -> deviation = 9.81 (> 3.0)
        every { accelerometerRepository.accelerationFlow() } returns flowOf(
            Acceleration(x = 0f, y = 0f, z = 0f)
        )
        val useCase = DetectTurbulenceUseCase(accelerometerRepository)

        val level = useCase.execute().first()

        assertThat(level).isEqualTo(TurbulenceLevel.SEVERE)
    }
}
