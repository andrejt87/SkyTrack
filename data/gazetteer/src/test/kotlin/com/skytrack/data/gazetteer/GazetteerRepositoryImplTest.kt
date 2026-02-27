package com.skytrack.data.gazetteer

import com.google.common.truth.Truth.assertThat
import com.skytrack.domain.model.LatLng
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GazetteerRepositoryImplTest {

    private lateinit var repository: GazetteerRepositoryImpl
    private val remoteDataSource: GazetteerRemoteDataSource = mockk()

    @Before
    fun setUp() {
        repository = GazetteerRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `reverseGeocode returns place name for valid coordinates`() = runTest {
        coEvery {
            remoteDataSource.reverseGeocode(52.52, 13.405)
        } returns GazetteerDto(placeName = "Berlin", countryCode = "DE", adminName = "Berlin")

        val result = repository.reverseGeocode(LatLng(52.52, 13.405))

        assertThat(result).isNotNull()
        assertThat(result!!.placeName).isEqualTo("Berlin")
        assertThat(result.countryCode).isEqualTo("DE")
    }

    @Test
    fun `reverseGeocode returns null for ocean coordinates`() = runTest {
        coEvery {
            remoteDataSource.reverseGeocode(0.0, 0.0)
        } returns null

        val result = repository.reverseGeocode(LatLng(0.0, 0.0))

        assertThat(result).isNull()
    }

    @Test
    fun `reverseGeocode maps DTO fields correctly`() = runTest {
        coEvery {
            remoteDataSource.reverseGeocode(51.5074, -0.1278)
        } returns GazetteerDto(
            placeName = "London",
            countryCode = "GB",
            adminName = "England"
        )

        val result = repository.reverseGeocode(LatLng(51.5074, -0.1278))

        assertThat(result!!.placeName).isEqualTo("London")
        assertThat(result.countryCode).isEqualTo("GB")
        assertThat(result.adminName).isEqualTo("England")
    }

    @Test
    fun `reverseGeocode propagates exception on network error`() = runTest {
        coEvery {
            remoteDataSource.reverseGeocode(any(), any())
        } throws RuntimeException("Network error")

        try {
            repository.reverseGeocode(LatLng(52.52, 13.405))
            // Should not reach here
            assertThat(false).isTrue()
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("Network error")
        }
    }
}
