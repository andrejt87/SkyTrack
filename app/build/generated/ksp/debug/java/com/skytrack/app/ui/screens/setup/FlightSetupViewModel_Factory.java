package com.skytrack.app.ui.screens.setup;

import com.skytrack.app.data.repository.AirportRepository;
import com.skytrack.app.data.repository.FlightRepository;
import com.skytrack.app.data.sensor.LocationProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class FlightSetupViewModel_Factory implements Factory<FlightSetupViewModel> {
  private final Provider<FlightRepository> flightRepositoryProvider;

  private final Provider<AirportRepository> airportRepositoryProvider;

  private final Provider<LocationProvider> locationProvider;

  public FlightSetupViewModel_Factory(Provider<FlightRepository> flightRepositoryProvider,
      Provider<AirportRepository> airportRepositoryProvider,
      Provider<LocationProvider> locationProvider) {
    this.flightRepositoryProvider = flightRepositoryProvider;
    this.airportRepositoryProvider = airportRepositoryProvider;
    this.locationProvider = locationProvider;
  }

  @Override
  public FlightSetupViewModel get() {
    return newInstance(flightRepositoryProvider.get(), airportRepositoryProvider.get(), locationProvider.get());
  }

  public static FlightSetupViewModel_Factory create(
      Provider<FlightRepository> flightRepositoryProvider,
      Provider<AirportRepository> airportRepositoryProvider,
      Provider<LocationProvider> locationProvider) {
    return new FlightSetupViewModel_Factory(flightRepositoryProvider, airportRepositoryProvider, locationProvider);
  }

  public static FlightSetupViewModel newInstance(FlightRepository flightRepository,
      AirportRepository airportRepository, LocationProvider locationProvider) {
    return new FlightSetupViewModel(flightRepository, airportRepository, locationProvider);
  }
}
