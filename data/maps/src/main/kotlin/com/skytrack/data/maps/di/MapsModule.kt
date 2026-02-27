package com.skytrack.data.maps.di

import android.content.Context
import com.skytrack.data.maps.MapTilesRepositoryImpl
import com.skytrack.domain.repository.MapTilesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapsModule {

    @Provides
    @Singleton
    fun provideMapTilesRepository(
        @ApplicationContext context: Context
    ): MapTilesRepository {
        return MapTilesRepositoryImpl(context)
    }
}
