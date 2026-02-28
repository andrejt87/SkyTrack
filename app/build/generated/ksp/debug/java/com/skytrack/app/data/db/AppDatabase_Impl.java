package com.skytrack.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile AirportDao _airportDao;

  private volatile FlightDao _flightDao;

  private volatile TrackPointDao _trackPointDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `airports` (`iata` TEXT NOT NULL, `icao` TEXT NOT NULL, `name` TEXT NOT NULL, `city` TEXT NOT NULL, `country` TEXT NOT NULL, `lat` REAL NOT NULL, `lon` REAL NOT NULL, `tz` TEXT NOT NULL, PRIMARY KEY(`iata`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `flights` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `departureIata` TEXT NOT NULL, `departureName` TEXT NOT NULL, `departureLat` REAL NOT NULL, `departureLon` REAL NOT NULL, `departureTz` TEXT NOT NULL, `arrivalIata` TEXT NOT NULL, `arrivalName` TEXT NOT NULL, `arrivalLat` REAL NOT NULL, `arrivalLon` REAL NOT NULL, `arrivalTz` TEXT NOT NULL, `airline` TEXT NOT NULL, `scheduledDepartureMs` INTEGER NOT NULL, `scheduledArrivalMs` INTEGER NOT NULL, `actualDepartureMs` INTEGER NOT NULL, `actualArrivalMs` INTEGER NOT NULL, `status` TEXT NOT NULL, `totalDistanceKm` REAL NOT NULL, `maxAltitudeM` REAL NOT NULL, `maxSpeedKmh` REAL NOT NULL, `avgSpeedKmh` REAL NOT NULL, `createdAtMs` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `track_points` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `flightId` INTEGER NOT NULL, `lat` REAL NOT NULL, `lon` REAL NOT NULL, `altitudeM` REAL NOT NULL, `speedKmh` REAL NOT NULL, `heading` REAL NOT NULL, `accuracy` REAL NOT NULL, `timestamp` INTEGER NOT NULL, FOREIGN KEY(`flightId`) REFERENCES `flights`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_track_points_flightId` ON `track_points` (`flightId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '9ec4bcd3bd64f9ca0569c474ac03a513')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `airports`");
        db.execSQL("DROP TABLE IF EXISTS `flights`");
        db.execSQL("DROP TABLE IF EXISTS `track_points`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsAirports = new HashMap<String, TableInfo.Column>(8);
        _columnsAirports.put("iata", new TableInfo.Column("iata", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAirports.put("icao", new TableInfo.Column("icao", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAirports.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAirports.put("city", new TableInfo.Column("city", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAirports.put("country", new TableInfo.Column("country", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAirports.put("lat", new TableInfo.Column("lat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAirports.put("lon", new TableInfo.Column("lon", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAirports.put("tz", new TableInfo.Column("tz", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAirports = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAirports = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAirports = new TableInfo("airports", _columnsAirports, _foreignKeysAirports, _indicesAirports);
        final TableInfo _existingAirports = TableInfo.read(db, "airports");
        if (!_infoAirports.equals(_existingAirports)) {
          return new RoomOpenHelper.ValidationResult(false, "airports(com.skytrack.app.data.model.Airport).\n"
                  + " Expected:\n" + _infoAirports + "\n"
                  + " Found:\n" + _existingAirports);
        }
        final HashMap<String, TableInfo.Column> _columnsFlights = new HashMap<String, TableInfo.Column>(22);
        _columnsFlights.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("departureIata", new TableInfo.Column("departureIata", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("departureName", new TableInfo.Column("departureName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("departureLat", new TableInfo.Column("departureLat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("departureLon", new TableInfo.Column("departureLon", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("departureTz", new TableInfo.Column("departureTz", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("arrivalIata", new TableInfo.Column("arrivalIata", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("arrivalName", new TableInfo.Column("arrivalName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("arrivalLat", new TableInfo.Column("arrivalLat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("arrivalLon", new TableInfo.Column("arrivalLon", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("arrivalTz", new TableInfo.Column("arrivalTz", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("airline", new TableInfo.Column("airline", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("scheduledDepartureMs", new TableInfo.Column("scheduledDepartureMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("scheduledArrivalMs", new TableInfo.Column("scheduledArrivalMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("actualDepartureMs", new TableInfo.Column("actualDepartureMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("actualArrivalMs", new TableInfo.Column("actualArrivalMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("totalDistanceKm", new TableInfo.Column("totalDistanceKm", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("maxAltitudeM", new TableInfo.Column("maxAltitudeM", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("maxSpeedKmh", new TableInfo.Column("maxSpeedKmh", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("avgSpeedKmh", new TableInfo.Column("avgSpeedKmh", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlights.put("createdAtMs", new TableInfo.Column("createdAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFlights = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFlights = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFlights = new TableInfo("flights", _columnsFlights, _foreignKeysFlights, _indicesFlights);
        final TableInfo _existingFlights = TableInfo.read(db, "flights");
        if (!_infoFlights.equals(_existingFlights)) {
          return new RoomOpenHelper.ValidationResult(false, "flights(com.skytrack.app.data.model.Flight).\n"
                  + " Expected:\n" + _infoFlights + "\n"
                  + " Found:\n" + _existingFlights);
        }
        final HashMap<String, TableInfo.Column> _columnsTrackPoints = new HashMap<String, TableInfo.Column>(9);
        _columnsTrackPoints.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrackPoints.put("flightId", new TableInfo.Column("flightId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrackPoints.put("lat", new TableInfo.Column("lat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrackPoints.put("lon", new TableInfo.Column("lon", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrackPoints.put("altitudeM", new TableInfo.Column("altitudeM", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrackPoints.put("speedKmh", new TableInfo.Column("speedKmh", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrackPoints.put("heading", new TableInfo.Column("heading", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrackPoints.put("accuracy", new TableInfo.Column("accuracy", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrackPoints.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTrackPoints = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysTrackPoints.add(new TableInfo.ForeignKey("flights", "CASCADE", "NO ACTION", Arrays.asList("flightId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTrackPoints = new HashSet<TableInfo.Index>(1);
        _indicesTrackPoints.add(new TableInfo.Index("index_track_points_flightId", false, Arrays.asList("flightId"), Arrays.asList("ASC")));
        final TableInfo _infoTrackPoints = new TableInfo("track_points", _columnsTrackPoints, _foreignKeysTrackPoints, _indicesTrackPoints);
        final TableInfo _existingTrackPoints = TableInfo.read(db, "track_points");
        if (!_infoTrackPoints.equals(_existingTrackPoints)) {
          return new RoomOpenHelper.ValidationResult(false, "track_points(com.skytrack.app.data.model.TrackPoint).\n"
                  + " Expected:\n" + _infoTrackPoints + "\n"
                  + " Found:\n" + _existingTrackPoints);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "9ec4bcd3bd64f9ca0569c474ac03a513", "a10fcb6daae77c24bd7a8f3232de7679");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "airports","flights","track_points");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `airports`");
      _db.execSQL("DELETE FROM `flights`");
      _db.execSQL("DELETE FROM `track_points`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(AirportDao.class, AirportDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FlightDao.class, FlightDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TrackPointDao.class, TrackPointDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public AirportDao airportDao() {
    if (_airportDao != null) {
      return _airportDao;
    } else {
      synchronized(this) {
        if(_airportDao == null) {
          _airportDao = new AirportDao_Impl(this);
        }
        return _airportDao;
      }
    }
  }

  @Override
  public FlightDao flightDao() {
    if (_flightDao != null) {
      return _flightDao;
    } else {
      synchronized(this) {
        if(_flightDao == null) {
          _flightDao = new FlightDao_Impl(this);
        }
        return _flightDao;
      }
    }
  }

  @Override
  public TrackPointDao trackPointDao() {
    if (_trackPointDao != null) {
      return _trackPointDao;
    } else {
      synchronized(this) {
        if(_trackPointDao == null) {
          _trackPointDao = new TrackPointDao_Impl(this);
        }
        return _trackPointDao;
      }
    }
  }
}
