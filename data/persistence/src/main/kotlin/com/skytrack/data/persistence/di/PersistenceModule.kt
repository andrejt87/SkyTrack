package com.skytrack.data.persistence.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.skytrack.data.persistence.FlightDao
import com.skytrack.data.persistence.FlightDatabase
import com.skytrack.data.persistence.FlightRepositoryImpl
import com.skytrack.data.persistence.SettingsRepositoryImpl
import com.skytrack.domain.repository.AirportRepository
import com.skytrack.domain.repository.FlightRepository
import com.skytrack.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "skytrack_settings")

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideFlightDatabase(
        @ApplicationContext context: Context
    ): FlightDatabase {
        return Room.databaseBuilder(
            context,
            FlightDatabase::class.java,
            "flights.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFlightDao(db: FlightDatabase): FlightDao = db.flightDao()

    @Provides
    @Singleton
    fun provideFlightRepository(
        flightDao: FlightDao,
        airportRepository: AirportRepository
    ): FlightRepository {
        return FlightRepositoryImpl(flightDao, airportRepository)
    }

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: DataStore<Preferences>
    ): SettingsRepository {
        return SettingsRepositoryImpl(dataStore)
    }
}
