package com.skytrack.app.service;

import com.skytrack.app.data.repository.FlightRepository;
import com.skytrack.app.data.repository.LocationRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class FlightTrackingService_MembersInjector implements MembersInjector<FlightTrackingService> {
  private final Provider<LocationRepository> locationRepositoryProvider;

  private final Provider<FlightRepository> flightRepositoryProvider;

  public FlightTrackingService_MembersInjector(
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<FlightRepository> flightRepositoryProvider) {
    this.locationRepositoryProvider = locationRepositoryProvider;
    this.flightRepositoryProvider = flightRepositoryProvider;
  }

  public static MembersInjector<FlightTrackingService> create(
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<FlightRepository> flightRepositoryProvider) {
    return new FlightTrackingService_MembersInjector(locationRepositoryProvider, flightRepositoryProvider);
  }

  @Override
  public void injectMembers(FlightTrackingService instance) {
    injectLocationRepository(instance, locationRepositoryProvider.get());
    injectFlightRepository(instance, flightRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.skytrack.app.service.FlightTrackingService.locationRepository")
  public static void injectLocationRepository(FlightTrackingService instance,
      LocationRepository locationRepository) {
    instance.locationRepository = locationRepository;
  }

  @InjectedFieldSignature("com.skytrack.app.service.FlightTrackingService.flightRepository")
  public static void injectFlightRepository(FlightTrackingService instance,
      FlightRepository flightRepository) {
    instance.flightRepository = flightRepository;
  }
}
