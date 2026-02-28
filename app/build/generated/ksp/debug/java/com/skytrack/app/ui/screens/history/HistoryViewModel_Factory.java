package com.skytrack.app.ui.screens.history;

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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<FlightRepository> flightRepositoryProvider;

  public HistoryViewModel_Factory(Provider<FlightRepository> flightRepositoryProvider) {
    this.flightRepositoryProvider = flightRepositoryProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(flightRepositoryProvider.get());
  }

  public static HistoryViewModel_Factory create(
      Provider<FlightRepository> flightRepositoryProvider) {
    return new HistoryViewModel_Factory(flightRepositoryProvider);
  }

  public static HistoryViewModel newInstance(FlightRepository flightRepository) {
    return new HistoryViewModel(flightRepository);
  }
}
