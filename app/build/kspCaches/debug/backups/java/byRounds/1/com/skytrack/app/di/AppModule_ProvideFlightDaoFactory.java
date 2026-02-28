package com.skytrack.app.di;

import com.skytrack.app.data.db.AppDatabase;
import com.skytrack.app.data.db.FlightDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideFlightDaoFactory implements Factory<FlightDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideFlightDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FlightDao get() {
    return provideFlightDao(dbProvider.get());
  }

  public static AppModule_ProvideFlightDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideFlightDaoFactory(dbProvider);
  }

  public static FlightDao provideFlightDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFlightDao(db));
  }
}
