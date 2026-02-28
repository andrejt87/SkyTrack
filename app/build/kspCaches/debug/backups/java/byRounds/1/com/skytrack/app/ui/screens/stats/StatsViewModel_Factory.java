package com.skytrack.app.ui.screens.stats;

import androidx.lifecycle.SavedStateHandle;
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
public final class StatsViewModel_Factory implements Factory<StatsViewModel> {
  private final Provider<FlightRepository> flightRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public StatsViewModel_Factory(Provider<FlightRepository> flightRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.flightRepositoryProvider = flightRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public StatsViewModel get() {
    return newInstance(flightRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static StatsViewModel_Factory create(Provider<FlightRepository> flightRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new StatsViewModel_Factory(flightRepositoryProvider, savedStateHandleProvider);
  }

  public static StatsViewModel newInstance(FlightRepository flightRepository,
      SavedStateHandle savedStateHandle) {
    return new StatsViewModel(flightRepository, savedStateHandle);
  }
}
