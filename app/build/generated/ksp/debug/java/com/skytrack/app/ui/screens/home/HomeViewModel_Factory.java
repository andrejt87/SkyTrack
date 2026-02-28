package com.skytrack.app.ui.screens.home;

import com.skytrack.app.data.repository.FlightRepository;
import com.skytrack.app.data.repository.LocationRepository;
import com.skytrack.app.data.sensor.BarometerProvider;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<FlightRepository> flightRepositoryProvider;

  private final Provider<LocationProvider> locationProvider;

  private final Provider<LocationRepository> locationRepositoryProvider;

  private final Provider<BarometerProvider> barometerProvider;

  public HomeViewModel_Factory(Provider<FlightRepository> flightRepositoryProvider,
      Provider<LocationProvider> locationProvider,
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<BarometerProvider> barometerProvider) {
    this.flightRepositoryProvider = flightRepositoryProvider;
    this.locationProvider = locationProvider;
    this.locationRepositoryProvider = locationRepositoryProvider;
    this.barometerProvider = barometerProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(flightRepositoryProvider.get(), locationProvider.get(), locationRepositoryProvider.get(), barometerProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<FlightRepository> flightRepositoryProvider,
      Provider<LocationProvider> locationProvider,
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<BarometerProvider> barometerProvider) {
    return new HomeViewModel_Factory(flightRepositoryProvider, locationProvider, locationRepositoryProvider, barometerProvider);
  }

  public static HomeViewModel newInstance(FlightRepository flightRepository,
      LocationProvider locationProvider, LocationRepository locationRepository,
      BarometerProvider barometerProvider) {
    return new HomeViewModel(flightRepository, locationProvider, locationRepository, barometerProvider);
  }
}
