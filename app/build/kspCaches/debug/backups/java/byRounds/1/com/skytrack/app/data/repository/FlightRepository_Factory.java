package com.skytrack.app.data.repository;

import com.skytrack.app.data.db.FlightDao;
import com.skytrack.app.data.db.TrackPointDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class FlightRepository_Factory implements Factory<FlightRepository> {
  private final Provider<FlightDao> flightDaoProvider;

  private final Provider<TrackPointDao> trackPointDaoProvider;

  public FlightRepository_Factory(Provider<FlightDao> flightDaoProvider,
      Provider<TrackPointDao> trackPointDaoProvider) {
    this.flightDaoProvider = flightDaoProvider;
    this.trackPointDaoProvider = trackPointDaoProvider;
  }

  @Override
  public FlightRepository get() {
    return newInstance(flightDaoProvider.get(), trackPointDaoProvider.get());
  }

  public static FlightRepository_Factory create(Provider<FlightDao> flightDaoProvider,
      Provider<TrackPointDao> trackPointDaoProvider) {
    return new FlightRepository_Factory(flightDaoProvider, trackPointDaoProvider);
  }

  public static FlightRepository newInstance(FlightDao flightDao, TrackPointDao trackPointDao) {
    return new FlightRepository(flightDao, trackPointDao);
  }
}
