package com.skytrack.app;

import com.skytrack.app.data.repository.AirportRepository;
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
public final class SkyTrackApplication_MembersInjector implements MembersInjector<SkyTrackApplication> {
  private final Provider<AirportRepository> airportRepositoryProvider;

  public SkyTrackApplication_MembersInjector(
      Provider<AirportRepository> airportRepositoryProvider) {
    this.airportRepositoryProvider = airportRepositoryProvider;
  }

  public static MembersInjector<SkyTrackApplication> create(
      Provider<AirportRepository> airportRepositoryProvider) {
    return new SkyTrackApplication_MembersInjector(airportRepositoryProvider);
  }

  @Override
  public void injectMembers(SkyTrackApplication instance) {
    injectAirportRepository(instance, airportRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.skytrack.app.SkyTrackApplication.airportRepository")
  public static void injectAirportRepository(SkyTrackApplication instance,
      AirportRepository airportRepository) {
    instance.airportRepository = airportRepository;
  }
}
