package com.skytrack.data.sensors.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.skytrack.data.sensors.AndroidAccelerometerRepositoryImpl
import com.skytrack.data.sensors.AndroidBarometerRepositoryImpl
import com.skytrack.data.sensors.FusedLocationRepositoryImpl
import com.skytrack.domain.repository.AccelerometerRepository
import com.skytrack.domain.repository.BarometerRepository
import com.skytrack.domain.repository.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorsModule {

    @Provides
    @Singleton
    fun provideFusedLocationClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        fusedLocationClient: FusedLocationProviderClient
    ): LocationRepository {
        return FusedLocationRepositoryImpl(fusedLocationClient)
    }

    @Provides
    @Singleton
    fun provideBarometerRepository(
        @ApplicationContext context: Context
    ): BarometerRepository {
        return AndroidBarometerRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideAccelerometerRepository(
        @ApplicationContext context: Context
    ): AccelerometerRepository {
        return AndroidAccelerometerRepositoryImpl(context)
    }
}
