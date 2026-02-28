package com.skytrack.app.ui.screens.dashboard;

import androidx.lifecycle.SavedStateHandle;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<FlightRepository> flightRepositoryProvider;

  private final Provider<LocationProvider> locationProvider;

  private final Provider<LocationRepository> locationRepositoryProvider;

  private final Provider<BarometerProvider> barometerProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public DashboardViewModel_Factory(Provider<FlightRepository> flightRepositoryProvider,
      Provider<LocationProvider> locationProvider,
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<BarometerProvider> barometerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.flightRepositoryProvider = flightRepositoryProvider;
    this.locationProvider = locationProvider;
    this.locationRepositoryProvider = locationRepositoryProvider;
    this.barometerProvider = barometerProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(flightRepositoryProvider.get(), locationProvider.get(), locationRepositoryProvider.get(), barometerProvider.get(), savedStateHandleProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<FlightRepository> flightRepositoryProvider,
      Provider<LocationProvider> locationProvider,
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<BarometerProvider> barometerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new DashboardViewModel_Factory(flightRepositoryProvider, locationProvider, locationRepositoryProvider, barometerProvider, savedStateHandleProvider);
  }

  public static DashboardViewModel newInstance(FlightRepository flightRepository,
      LocationProvider locationProvider, LocationRepository locationRepository,
      BarometerProvider barometerProvider, SavedStateHandle savedStateHandle) {
    return new DashboardViewModel(flightRepository, locationProvider, locationRepository, barometerProvider, savedStateHandle);
  }
}
