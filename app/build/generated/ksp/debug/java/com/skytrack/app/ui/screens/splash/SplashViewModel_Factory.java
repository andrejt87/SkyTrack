package com.skytrack.app.ui.screens.splash;

import com.skytrack.app.data.repository.FlightRepository;
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
public final class SplashViewModel_Factory implements Factory<SplashViewModel> {
  private final Provider<FlightRepository> flightRepositoryProvider;

  public SplashViewModel_Factory(Provider<FlightRepository> flightRepositoryProvider) {
    this.flightRepositoryProvider = flightRepositoryProvider;
  }

  @Override
  public SplashViewModel get() {
    return newInstance(flightRepositoryProvider.get());
  }

  public static SplashViewModel_Factory create(
      Provider<FlightRepository> flightRepositoryProvider) {
    return new SplashViewModel_Factory(flightRepositoryProvider);
  }

  public static SplashViewModel newInstance(FlightRepository flightRepository) {
    return new SplashViewModel(flightRepository);
  }
}
