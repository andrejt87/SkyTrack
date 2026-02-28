package com.skytrack.app.di;

import com.skytrack.app.data.db.AppDatabase;
import com.skytrack.app.data.db.TrackPointDao;
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
public final class AppModule_ProvideTrackPointDaoFactory implements Factory<TrackPointDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideTrackPointDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TrackPointDao get() {
    return provideTrackPointDao(dbProvider.get());
  }

  public static AppModule_ProvideTrackPointDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideTrackPointDaoFactory(dbProvider);
  }

  public static TrackPointDao provideTrackPointDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTrackPointDao(db));
  }
}
