package com.skytrack.app.di

import com.skytrack.domain.repository.*
import com.skytrack.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideCalculateFlightProgressUseCase(): CalculateFlightProgressUseCase {
        return CalculateFlightProgressUseCase()
    }

    @Provides
    fun provideGetCurrentPositionUseCase(
        locationRepository: LocationRepository
    ): GetCurrentPositionUseCase {
        return GetCurrentPositionUseCase(locationRepository)
    }

    @Provides
    fun provideSearchAirportUseCase(
        airportRepository: AirportRepository
    ): SearchAirportUseCase {
        return SearchAirportUseCase(airportRepository)
    }

    @Provides
    fun provideGetNearestAirportUseCase(
        airportRepository: AirportRepository,
        locationRepository: LocationRepository
    ): GetNearestAirportUseCase {
        return GetNearestAirportUseCase(airportRepository, locationRepository)
    }

    @Provides
    fun provideStartFlightUseCase(
        flightRepository: FlightRepository,
        airportRepository: AirportRepository
    ): StartFlightUseCase {
        return StartFlightUseCase(flightRepository, airportRepository)
    }

    @Provides
    fun provideGetRecentFlightsUseCase(
        flightRepository: FlightRepository
    ): GetRecentFlightsUseCase {
        return GetRecentFlightsUseCase(flightRepository)
    }

    @Provides
    fun provideCalculateEtaUseCase(): CalculateEtaUseCase {
        return CalculateEtaUseCase()
    }

    @Provides
    fun provideGetOverflyInfoUseCase(
        gazetteerRepository: GazetteerRepository
    ): GetOverflyInfoUseCase {
        return GetOverflyInfoUseCase(gazetteerRepository)
    }

    @Provides
    fun provideGetTimezoneUseCase(
        gazetteerRepository: GazetteerRepository
    ): GetTimezoneUseCase {
        return GetTimezoneUseCase(gazetteerRepository)
    }

    @Provides
    fun provideGetFlightStatsUseCase(
        flightRepository: FlightRepository
    ): GetFlightStatsUseCase {
        return GetFlightStatsUseCase(flightRepository)
    }

    @Provides
    fun provideDetectTurbulenceUseCase(
        accelerometerRepository: AccelerometerRepository
    ): DetectTurbulenceUseCase {
        return DetectTurbulenceUseCase(accelerometerRepository)
    }

    @Provides
    fun provideObserveActiveFlightUseCase(
        flightRepository: FlightRepository
    ): ObserveActiveFlightUseCase {
        return ObserveActiveFlightUseCase(flightRepository)
    }
}
