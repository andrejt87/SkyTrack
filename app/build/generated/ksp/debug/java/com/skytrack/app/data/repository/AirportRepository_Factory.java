package com.skytrack.app.data.repository;

import android.content.Context;
import com.google.gson.Gson;
import com.skytrack.app.data.db.AirportDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AirportRepository_Factory implements Factory<AirportRepository> {
  private final Provider<AirportDao> airportDaoProvider;

  private final Provider<Context> contextProvider;

  private final Provider<Gson> gsonProvider;

  public AirportRepository_Factory(Provider<AirportDao> airportDaoProvider,
      Provider<Context> contextProvider, Provider<Gson> gsonProvider) {
    this.airportDaoProvider = airportDaoProvider;
    this.contextProvider = contextProvider;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public AirportRepository get() {
    return newInstance(airportDaoProvider.get(), contextProvider.get(), gsonProvider.get());
  }

  public static AirportRepository_Factory create(Provider<AirportDao> airportDaoProvider,
      Provider<Context> contextProvider, Provider<Gson> gsonProvider) {
    return new AirportRepository_Factory(airportDaoProvider, contextProvider, gsonProvider);
  }

  public static AirportRepository newInstance(AirportDao airportDao, Context context, Gson gson) {
    return new AirportRepository(airportDao, context, gson);
  }
}
