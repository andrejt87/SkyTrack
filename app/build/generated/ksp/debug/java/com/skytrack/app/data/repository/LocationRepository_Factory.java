package com.skytrack.app.data.repository;

import com.skytrack.app.data.sensor.LocationProvider;
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
public final class LocationRepository_Factory implements Factory<LocationRepository> {
  private final Provider<LocationProvider> locationProvider;

  public LocationRepository_Factory(Provider<LocationProvider> locationProvider) {
    this.locationProvider = locationProvider;
  }

  @Override
  public LocationRepository get() {
    return newInstance(locationProvider.get());
  }

  public static LocationRepository_Factory create(Provider<LocationProvider> locationProvider) {
    return new LocationRepository_Factory(locationProvider);
  }

  public static LocationRepository newInstance(LocationProvider locationProvider) {
    return new LocationRepository(locationProvider);
  }
}
