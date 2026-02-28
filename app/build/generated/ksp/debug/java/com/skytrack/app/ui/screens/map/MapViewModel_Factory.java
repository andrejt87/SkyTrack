package com.skytrack.app.ui.screens.map;

import androidx.lifecycle.SavedStateHandle;
import com.skytrack.app.data.repository.FlightRepository;
import com.skytrack.app.data.repository.LocationRepository;
import com.skytrack.app.data.sensor.BarometerProvider;
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
public final class MapViewModel_Factory implements Factory<MapViewModel> {
  private final Provider<FlightRepository> flightRepositoryProvider;

  private final Provider<LocationRepository> locationRepositoryProvider;

  private final Provider<BarometerProvider> barometerProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public MapViewModel_Factory(Provider<FlightRepository> flightRepositoryProvider,
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<BarometerProvider> barometerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.flightRepositoryProvider = flightRepositoryProvider;
    this.locationRepositoryProvider = locationRepositoryProvider;
    this.barometerProvider = barometerProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public MapViewModel get() {
    return newInstance(flightRepositoryProvider.get(), locationRepositoryProvider.get(), barometerProvider.get(), savedStateHandleProvider.get());
  }

  public static MapViewModel_Factory create(Provider<FlightRepository> flightRepositoryProvider,
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<BarometerProvider> barometerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new MapViewModel_Factory(flightRepositoryProvider, locationRepositoryProvider, barometerProvider, savedStateHandleProvider);
  }

  public static MapViewModel newInstance(FlightRepository flightRepository,
      LocationRepository locationRepository, BarometerProvider barometerProvider,
      SavedStateHandle savedStateHandle) {
    return new MapViewModel(flightRepository, locationRepository, barometerProvider, savedStateHandle);
  }
}
