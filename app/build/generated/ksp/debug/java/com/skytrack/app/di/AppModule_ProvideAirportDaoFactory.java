package com.skytrack.app.di;

import com.skytrack.app.data.db.AirportDao;
import com.skytrack.app.data.db.AppDatabase;
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
public final class AppModule_ProvideAirportDaoFactory implements Factory<AirportDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideAirportDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AirportDao get() {
    return provideAirportDao(dbProvider.get());
  }

  public static AppModule_ProvideAirportDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideAirportDaoFactory(dbProvider);
  }

  public static AirportDao provideAirportDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAirportDao(db));
  }
}
