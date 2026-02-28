package com.skytrack.app.ui.airportpicker;

import androidx.lifecycle.SavedStateHandle;
import com.skytrack.app.data.repository.AirportRepository;
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
public final class AirportPickerViewModel_Factory implements Factory<AirportPickerViewModel> {
  private final Provider<AirportRepository> airportRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public AirportPickerViewModel_Factory(Provider<AirportRepository> airportRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.airportRepositoryProvider = airportRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public AirportPickerViewModel get() {
    return newInstance(airportRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static AirportPickerViewModel_Factory create(
      Provider<AirportRepository> airportRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new AirportPickerViewModel_Factory(airportRepositoryProvider, savedStateHandleProvider);
  }

  public static AirportPickerViewModel newInstance(AirportRepository airportRepository,
      SavedStateHandle savedStateHandle) {
    return new AirportPickerViewModel(airportRepository, savedStateHandle);
  }
}
