package com.skytrack.data.gazetteer.di

import android.content.Context
import androidx.room.Room
import com.skytrack.data.gazetteer.GazetteerDao
import com.skytrack.data.gazetteer.GazetteerDatabase
import com.skytrack.data.gazetteer.GazetteerRepositoryImpl
import com.skytrack.domain.repository.GazetteerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GazetteerModule {

    @Provides
    @Singleton
    fun provideGazetteerDatabase(
        @ApplicationContext context: Context
    ): GazetteerDatabase {
        return Room.databaseBuilder(
            context,
            GazetteerDatabase::class.java,
            "gazetteer.db"
        ).createFromAsset("databases/gazetteer.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideGazetteerDao(db: GazetteerDatabase): GazetteerDao = db.gazetteerDao()

    @Provides
    @Singleton
    fun provideGazetteerRepository(dao: GazetteerDao): GazetteerRepository {
        return GazetteerRepositoryImpl(dao)
    }
}
