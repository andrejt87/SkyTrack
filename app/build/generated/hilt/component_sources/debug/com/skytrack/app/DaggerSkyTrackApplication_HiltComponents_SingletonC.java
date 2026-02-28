package com.skytrack.app;

import android.app.Activity;
import android.app.Service;
import android.content.SharedPreferences;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.gson.Gson;
import com.skytrack.app.data.db.AirportDao;
import com.skytrack.app.data.db.AppDatabase;
import com.skytrack.app.data.db.FlightDao;
import com.skytrack.app.data.db.TrackPointDao;
import com.skytrack.app.data.repository.AirportRepository;
import com.skytrack.app.data.repository.FlightRepository;
import com.skytrack.app.data.repository.LocationRepository;
import com.skytrack.app.data.sensor.BarometerProvider;
import com.skytrack.app.data.sensor.LocationProvider;
import com.skytrack.app.di.AppModule_ProvideAirportDaoFactory;
import com.skytrack.app.di.AppModule_ProvideDatabaseFactory;
import com.skytrack.app.di.AppModule_ProvideFlightDaoFactory;
import com.skytrack.app.di.AppModule_ProvideGsonFactory;
import com.skytrack.app.di.AppModule_ProvideSharedPreferencesFactory;
import com.skytrack.app.di.AppModule_ProvideTrackPointDaoFactory;
import com.skytrack.app.service.FlightTrackingService;
import com.skytrack.app.service.FlightTrackingService_MembersInjector;
import com.skytrack.app.ui.airportpicker.AirportPickerViewModel;
import com.skytrack.app.ui.airportpicker.AirportPickerViewModel_HiltModules;
import com.skytrack.app.ui.screens.dashboard.DashboardViewModel;
import com.skytrack.app.ui.screens.dashboard.DashboardViewModel_HiltModules;
import com.skytrack.app.ui.screens.history.HistoryViewModel;
import com.skytrack.app.ui.screens.history.HistoryViewModel_HiltModules;
import com.skytrack.app.ui.screens.home.HomeViewModel;
import com.skytrack.app.ui.screens.home.HomeViewModel_HiltModules;
import com.skytrack.app.ui.screens.map.MapViewModel;
import com.skytrack.app.ui.screens.map.MapViewModel_HiltModules;
import com.skytrack.app.ui.screens.settings.SettingsViewModel;
import com.skytrack.app.ui.screens.settings.SettingsViewModel_HiltModules;
import com.skytrack.app.ui.screens.setup.FlightSetupViewModel;
import com.skytrack.app.ui.screens.setup.FlightSetupViewModel_HiltModules;
import com.skytrack.app.ui.screens.splash.SplashViewModel;
import com.skytrack.app.ui.screens.splash.SplashViewModel_HiltModules;
import com.skytrack.app.ui.screens.stats.StatsViewModel;
import com.skytrack.app.ui.screens.stats.StatsViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerSkyTrackApplication_HiltComponents_SingletonC {
  private DaggerSkyTrackApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public SkyTrackApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements SkyTrackApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public SkyTrackApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements SkyTrackApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public SkyTrackApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements SkyTrackApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public SkyTrackApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements SkyTrackApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SkyTrackApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements SkyTrackApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SkyTrackApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements SkyTrackApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public SkyTrackApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements SkyTrackApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public SkyTrackApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends SkyTrackApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends SkyTrackApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends SkyTrackApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends SkyTrackApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(9).put(LazyClassKeyProvider.com_skytrack_app_ui_airportpicker_AirportPickerViewModel, AirportPickerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_dashboard_DashboardViewModel, DashboardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_setup_FlightSetupViewModel, FlightSetupViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_history_HistoryViewModel, HistoryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_map_MapViewModel, MapViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_splash_SplashViewModel, SplashViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_stats_StatsViewModel, StatsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_skytrack_app_ui_screens_setup_FlightSetupViewModel = "com.skytrack.app.ui.screens.setup.FlightSetupViewModel";

      static String com_skytrack_app_ui_screens_dashboard_DashboardViewModel = "com.skytrack.app.ui.screens.dashboard.DashboardViewModel";

      static String com_skytrack_app_ui_screens_history_HistoryViewModel = "com.skytrack.app.ui.screens.history.HistoryViewModel";

      static String com_skytrack_app_ui_screens_stats_StatsViewModel = "com.skytrack.app.ui.screens.stats.StatsViewModel";

      static String com_skytrack_app_ui_airportpicker_AirportPickerViewModel = "com.skytrack.app.ui.airportpicker.AirportPickerViewModel";

      static String com_skytrack_app_ui_screens_map_MapViewModel = "com.skytrack.app.ui.screens.map.MapViewModel";

      static String com_skytrack_app_ui_screens_splash_SplashViewModel = "com.skytrack.app.ui.screens.splash.SplashViewModel";

      static String com_skytrack_app_ui_screens_home_HomeViewModel = "com.skytrack.app.ui.screens.home.HomeViewModel";

      static String com_skytrack_app_ui_screens_settings_SettingsViewModel = "com.skytrack.app.ui.screens.settings.SettingsViewModel";

      @KeepFieldType
      FlightSetupViewModel com_skytrack_app_ui_screens_setup_FlightSetupViewModel2;

      @KeepFieldType
      DashboardViewModel com_skytrack_app_ui_screens_dashboard_DashboardViewModel2;

      @KeepFieldType
      HistoryViewModel com_skytrack_app_ui_screens_history_HistoryViewModel2;

      @KeepFieldType
      StatsViewModel com_skytrack_app_ui_screens_stats_StatsViewModel2;

      @KeepFieldType
      AirportPickerViewModel com_skytrack_app_ui_airportpicker_AirportPickerViewModel2;

      @KeepFieldType
      MapViewModel com_skytrack_app_ui_screens_map_MapViewModel2;

      @KeepFieldType
      SplashViewModel com_skytrack_app_ui_screens_splash_SplashViewModel2;

      @KeepFieldType
      HomeViewModel com_skytrack_app_ui_screens_home_HomeViewModel2;

      @KeepFieldType
      SettingsViewModel com_skytrack_app_ui_screens_settings_SettingsViewModel2;
    }
  }

  private static final class ViewModelCImpl extends SkyTrackApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AirportPickerViewModel> airportPickerViewModelProvider;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<FlightSetupViewModel> flightSetupViewModelProvider;

    private Provider<HistoryViewModel> historyViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<MapViewModel> mapViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<SplashViewModel> splashViewModelProvider;

    private Provider<StatsViewModel> statsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.airportPickerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.flightSetupViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.historyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.mapViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.splashViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.statsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(9).put(LazyClassKeyProvider.com_skytrack_app_ui_airportpicker_AirportPickerViewModel, ((Provider) airportPickerViewModelProvider)).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_dashboard_DashboardViewModel, ((Provider) dashboardViewModelProvider)).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_setup_FlightSetupViewModel, ((Provider) flightSetupViewModelProvider)).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_history_HistoryViewModel, ((Provider) historyViewModelProvider)).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_map_MapViewModel, ((Provider) mapViewModelProvider)).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_splash_SplashViewModel, ((Provider) splashViewModelProvider)).put(LazyClassKeyProvider.com_skytrack_app_ui_screens_stats_StatsViewModel, ((Provider) statsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_skytrack_app_ui_screens_setup_FlightSetupViewModel = "com.skytrack.app.ui.screens.setup.FlightSetupViewModel";

      static String com_skytrack_app_ui_screens_splash_SplashViewModel = "com.skytrack.app.ui.screens.splash.SplashViewModel";

      static String com_skytrack_app_ui_screens_history_HistoryViewModel = "com.skytrack.app.ui.screens.history.HistoryViewModel";

      static String com_skytrack_app_ui_screens_dashboard_DashboardViewModel = "com.skytrack.app.ui.screens.dashboard.DashboardViewModel";

      static String com_skytrack_app_ui_screens_stats_StatsViewModel = "com.skytrack.app.ui.screens.stats.StatsViewModel";

      static String com_skytrack_app_ui_screens_map_MapViewModel = "com.skytrack.app.ui.screens.map.MapViewModel";

      static String com_skytrack_app_ui_airportpicker_AirportPickerViewModel = "com.skytrack.app.ui.airportpicker.AirportPickerViewModel";

      static String com_skytrack_app_ui_screens_home_HomeViewModel = "com.skytrack.app.ui.screens.home.HomeViewModel";

      static String com_skytrack_app_ui_screens_settings_SettingsViewModel = "com.skytrack.app.ui.screens.settings.SettingsViewModel";

      @KeepFieldType
      FlightSetupViewModel com_skytrack_app_ui_screens_setup_FlightSetupViewModel2;

      @KeepFieldType
      SplashViewModel com_skytrack_app_ui_screens_splash_SplashViewModel2;

      @KeepFieldType
      HistoryViewModel com_skytrack_app_ui_screens_history_HistoryViewModel2;

      @KeepFieldType
      DashboardViewModel com_skytrack_app_ui_screens_dashboard_DashboardViewModel2;

      @KeepFieldType
      StatsViewModel com_skytrack_app_ui_screens_stats_StatsViewModel2;

      @KeepFieldType
      MapViewModel com_skytrack_app_ui_screens_map_MapViewModel2;

      @KeepFieldType
      AirportPickerViewModel com_skytrack_app_ui_airportpicker_AirportPickerViewModel2;

      @KeepFieldType
      HomeViewModel com_skytrack_app_ui_screens_home_HomeViewModel2;

      @KeepFieldType
      SettingsViewModel com_skytrack_app_ui_screens_settings_SettingsViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.skytrack.app.ui.airportpicker.AirportPickerViewModel 
          return (T) new AirportPickerViewModel(singletonCImpl.airportRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          case 1: // com.skytrack.app.ui.screens.dashboard.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.flightRepositoryProvider.get(), singletonCImpl.locationProvider.get(), singletonCImpl.locationRepositoryProvider.get(), singletonCImpl.barometerProvider.get(), viewModelCImpl.savedStateHandle);

          case 2: // com.skytrack.app.ui.screens.setup.FlightSetupViewModel 
          return (T) new FlightSetupViewModel(singletonCImpl.flightRepositoryProvider.get(), singletonCImpl.airportRepositoryProvider.get(), singletonCImpl.locationProvider.get());

          case 3: // com.skytrack.app.ui.screens.history.HistoryViewModel 
          return (T) new HistoryViewModel(singletonCImpl.flightRepositoryProvider.get());

          case 4: // com.skytrack.app.ui.screens.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.flightRepositoryProvider.get(), singletonCImpl.locationProvider.get(), singletonCImpl.locationRepositoryProvider.get(), singletonCImpl.barometerProvider.get());

          case 5: // com.skytrack.app.ui.screens.map.MapViewModel 
          return (T) new MapViewModel(singletonCImpl.flightRepositoryProvider.get(), singletonCImpl.locationRepositoryProvider.get(), singletonCImpl.barometerProvider.get(), viewModelCImpl.savedStateHandle);

          case 6: // com.skytrack.app.ui.screens.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.provideSharedPreferencesProvider.get());

          case 7: // com.skytrack.app.ui.screens.splash.SplashViewModel 
          return (T) new SplashViewModel(singletonCImpl.flightRepositoryProvider.get());

          case 8: // com.skytrack.app.ui.screens.stats.StatsViewModel 
          return (T) new StatsViewModel(singletonCImpl.flightRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends SkyTrackApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends SkyTrackApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectFlightTrackingService(FlightTrackingService flightTrackingService) {
      injectFlightTrackingService2(flightTrackingService);
    }

    private FlightTrackingService injectFlightTrackingService2(FlightTrackingService instance) {
      FlightTrackingService_MembersInjector.injectLocationRepository(instance, singletonCImpl.locationRepositoryProvider.get());
      FlightTrackingService_MembersInjector.injectFlightRepository(instance, singletonCImpl.flightRepositoryProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends SkyTrackApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AppDatabase> provideDatabaseProvider;

    private Provider<AirportDao> provideAirportDaoProvider;

    private Provider<Gson> provideGsonProvider;

    private Provider<AirportRepository> airportRepositoryProvider;

    private Provider<FlightDao> provideFlightDaoProvider;

    private Provider<TrackPointDao> provideTrackPointDaoProvider;

    private Provider<FlightRepository> flightRepositoryProvider;

    private Provider<LocationProvider> locationProvider;

    private Provider<LocationRepository> locationRepositoryProvider;

    private Provider<BarometerProvider> barometerProvider;

    private Provider<SharedPreferences> provideSharedPreferencesProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 2));
      this.provideAirportDaoProvider = DoubleCheck.provider(new SwitchingProvider<AirportDao>(singletonCImpl, 1));
      this.provideGsonProvider = DoubleCheck.provider(new SwitchingProvider<Gson>(singletonCImpl, 3));
      this.airportRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AirportRepository>(singletonCImpl, 0));
      this.provideFlightDaoProvider = DoubleCheck.provider(new SwitchingProvider<FlightDao>(singletonCImpl, 5));
      this.provideTrackPointDaoProvider = DoubleCheck.provider(new SwitchingProvider<TrackPointDao>(singletonCImpl, 6));
      this.flightRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FlightRepository>(singletonCImpl, 4));
      this.locationProvider = DoubleCheck.provider(new SwitchingProvider<LocationProvider>(singletonCImpl, 7));
      this.locationRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<LocationRepository>(singletonCImpl, 8));
      this.barometerProvider = DoubleCheck.provider(new SwitchingProvider<BarometerProvider>(singletonCImpl, 9));
      this.provideSharedPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<SharedPreferences>(singletonCImpl, 10));
    }

    @Override
    public void injectSkyTrackApplication(SkyTrackApplication skyTrackApplication) {
      injectSkyTrackApplication2(skyTrackApplication);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private SkyTrackApplication injectSkyTrackApplication2(SkyTrackApplication instance) {
      SkyTrackApplication_MembersInjector.injectAirportRepository(instance, airportRepositoryProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.skytrack.app.data.repository.AirportRepository 
          return (T) new AirportRepository(singletonCImpl.provideAirportDaoProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideGsonProvider.get());

          case 1: // com.skytrack.app.data.db.AirportDao 
          return (T) AppModule_ProvideAirportDaoFactory.provideAirportDao(singletonCImpl.provideDatabaseProvider.get());

          case 2: // com.skytrack.app.data.db.AppDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.google.gson.Gson 
          return (T) AppModule_ProvideGsonFactory.provideGson();

          case 4: // com.skytrack.app.data.repository.FlightRepository 
          return (T) new FlightRepository(singletonCImpl.provideFlightDaoProvider.get(), singletonCImpl.provideTrackPointDaoProvider.get());

          case 5: // com.skytrack.app.data.db.FlightDao 
          return (T) AppModule_ProvideFlightDaoFactory.provideFlightDao(singletonCImpl.provideDatabaseProvider.get());

          case 6: // com.skytrack.app.data.db.TrackPointDao 
          return (T) AppModule_ProvideTrackPointDaoFactory.provideTrackPointDao(singletonCImpl.provideDatabaseProvider.get());

          case 7: // com.skytrack.app.data.sensor.LocationProvider 
          return (T) new LocationProvider(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.skytrack.app.data.repository.LocationRepository 
          return (T) new LocationRepository(singletonCImpl.locationProvider.get());

          case 9: // com.skytrack.app.data.sensor.BarometerProvider 
          return (T) new BarometerProvider(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // android.content.SharedPreferences 
          return (T) AppModule_ProvideSharedPreferencesFactory.provideSharedPreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
