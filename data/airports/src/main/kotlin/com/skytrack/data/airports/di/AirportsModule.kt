package com.skytrack.data.airports.di

import android.content.Context
import androidx.room.Room
import com.skytrack.data.airports.AirportDao
import com.skytrack.data.airports.AirportDatabase
import com.skytrack.data.airports.AirportRepositoryImpl
import com.skytrack.domain.repository.AirportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AirportsModule {

    @Provides
    @Singleton
    fun provideAirportDatabase(
        @ApplicationContext context: Context
    ): AirportDatabase {
        return Room.databaseBuilder(
            context,
            AirportDatabase::class.java,
            "airports.db"
        ).createFromAsset("databases/airports.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideAirportDao(db: AirportDatabase): AirportDao = db.airportDao()

    @Provides
    @Singleton
    fun provideAirportRepository(dao: AirportDao): AirportRepository {
        return AirportRepositoryImpl(dao)
    }
}
