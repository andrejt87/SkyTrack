package com.skytrack.app.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.skytrack.app.data.model.Flight;
import com.skytrack.app.data.model.FlightStatus;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FlightDao_Impl implements FlightDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Flight> __insertionAdapterOfFlight;

  private final EntityDeletionOrUpdateAdapter<Flight> __deletionAdapterOfFlight;

  private final EntityDeletionOrUpdateAdapter<Flight> __updateAdapterOfFlight;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public FlightDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFlight = new EntityInsertionAdapter<Flight>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `flights` (`id`,`departureIata`,`departureName`,`departureLat`,`departureLon`,`departureTz`,`arrivalIata`,`arrivalName`,`arrivalLat`,`arrivalLon`,`arrivalTz`,`airline`,`scheduledDepartureMs`,`scheduledArrivalMs`,`actualDepartureMs`,`actualArrivalMs`,`status`,`totalDistanceKm`,`maxAltitudeM`,`maxSpeedKmh`,`avgSpeedKmh`,`createdAtMs`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Flight entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDepartureIata());
        statement.bindString(3, entity.getDepartureName());
        statement.bindDouble(4, entity.getDepartureLat());
        statement.bindDouble(5, entity.getDepartureLon());
        statement.bindString(6, entity.getDepartureTz());
        statement.bindString(7, entity.getArrivalIata());
        statement.bindString(8, entity.getArrivalName());
        statement.bindDouble(9, entity.getArrivalLat());
        statement.bindDouble(10, entity.getArrivalLon());
        statement.bindString(11, entity.getArrivalTz());
        statement.bindString(12, entity.getAirline());
        statement.bindLong(13, entity.getScheduledDepartureMs());
        statement.bindLong(14, entity.getScheduledArrivalMs());
        statement.bindLong(15, entity.getActualDepartureMs());
        statement.bindLong(16, entity.getActualArrivalMs());
        statement.bindString(17, __FlightStatus_enumToString(entity.getStatus()));
        statement.bindDouble(18, entity.getTotalDistanceKm());
        statement.bindDouble(19, entity.getMaxAltitudeM());
        statement.bindDouble(20, entity.getMaxSpeedKmh());
        statement.bindDouble(21, entity.getAvgSpeedKmh());
        statement.bindLong(22, entity.getCreatedAtMs());
      }
    };
    this.__deletionAdapterOfFlight = new EntityDeletionOrUpdateAdapter<Flight>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `flights` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Flight entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFlight = new EntityDeletionOrUpdateAdapter<Flight>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `flights` SET `id` = ?,`departureIata` = ?,`departureName` = ?,`departureLat` = ?,`departureLon` = ?,`departureTz` = ?,`arrivalIata` = ?,`arrivalName` = ?,`arrivalLat` = ?,`arrivalLon` = ?,`arrivalTz` = ?,`airline` = ?,`scheduledDepartureMs` = ?,`scheduledArrivalMs` = ?,`actualDepartureMs` = ?,`actualArrivalMs` = ?,`status` = ?,`totalDistanceKm` = ?,`maxAltitudeM` = ?,`maxSpeedKmh` = ?,`avgSpeedKmh` = ?,`createdAtMs` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Flight entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDepartureIata());
        statement.bindString(3, entity.getDepartureName());
        statement.bindDouble(4, entity.getDepartureLat());
        statement.bindDouble(5, entity.getDepartureLon());
        statement.bindString(6, entity.getDepartureTz());
        statement.bindString(7, entity.getArrivalIata());
        statement.bindString(8, entity.getArrivalName());
        statement.bindDouble(9, entity.getArrivalLat());
        statement.bindDouble(10, entity.getArrivalLon());
        statement.bindString(11, entity.getArrivalTz());
        statement.bindString(12, entity.getAirline());
        statement.bindLong(13, entity.getScheduledDepartureMs());
        statement.bindLong(14, entity.getScheduledArrivalMs());
        statement.bindLong(15, entity.getActualDepartureMs());
        statement.bindLong(16, entity.getActualArrivalMs());
        statement.bindString(17, __FlightStatus_enumToString(entity.getStatus()));
        statement.bindDouble(18, entity.getTotalDistanceKm());
        statement.bindDouble(19, entity.getMaxAltitudeM());
        statement.bindDouble(20, entity.getMaxSpeedKmh());
        statement.bindDouble(21, entity.getAvgSpeedKmh());
        statement.bindLong(22, entity.getCreatedAtMs());
        statement.bindLong(23, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM flights WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Flight flight, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFlight.insertAndReturnId(flight);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Flight flight, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFlight.handle(flight);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Flight flight, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFlight.handle(flight);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Flight>> getAllFlights() {
    final String _sql = "SELECT * FROM flights ORDER BY createdAtMs DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flights"}, new Callable<List<Flight>>() {
      @Override
      @NonNull
      public List<Flight> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDepartureIata = CursorUtil.getColumnIndexOrThrow(_cursor, "departureIata");
          final int _cursorIndexOfDepartureName = CursorUtil.getColumnIndexOrThrow(_cursor, "departureName");
          final int _cursorIndexOfDepartureLat = CursorUtil.getColumnIndexOrThrow(_cursor, "departureLat");
          final int _cursorIndexOfDepartureLon = CursorUtil.getColumnIndexOrThrow(_cursor, "departureLon");
          final int _cursorIndexOfDepartureTz = CursorUtil.getColumnIndexOrThrow(_cursor, "departureTz");
          final int _cursorIndexOfArrivalIata = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalIata");
          final int _cursorIndexOfArrivalName = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalName");
          final int _cursorIndexOfArrivalLat = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalLat");
          final int _cursorIndexOfArrivalLon = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalLon");
          final int _cursorIndexOfArrivalTz = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalTz");
          final int _cursorIndexOfAirline = CursorUtil.getColumnIndexOrThrow(_cursor, "airline");
          final int _cursorIndexOfScheduledDepartureMs = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledDepartureMs");
          final int _cursorIndexOfScheduledArrivalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledArrivalMs");
          final int _cursorIndexOfActualDepartureMs = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDepartureMs");
          final int _cursorIndexOfActualArrivalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "actualArrivalMs");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTotalDistanceKm = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistanceKm");
          final int _cursorIndexOfMaxAltitudeM = CursorUtil.getColumnIndexOrThrow(_cursor, "maxAltitudeM");
          final int _cursorIndexOfMaxSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "maxSpeedKmh");
          final int _cursorIndexOfAvgSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSpeedKmh");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final List<Flight> _result = new ArrayList<Flight>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Flight _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDepartureIata;
            _tmpDepartureIata = _cursor.getString(_cursorIndexOfDepartureIata);
            final String _tmpDepartureName;
            _tmpDepartureName = _cursor.getString(_cursorIndexOfDepartureName);
            final double _tmpDepartureLat;
            _tmpDepartureLat = _cursor.getDouble(_cursorIndexOfDepartureLat);
            final double _tmpDepartureLon;
            _tmpDepartureLon = _cursor.getDouble(_cursorIndexOfDepartureLon);
            final String _tmpDepartureTz;
            _tmpDepartureTz = _cursor.getString(_cursorIndexOfDepartureTz);
            final String _tmpArrivalIata;
            _tmpArrivalIata = _cursor.getString(_cursorIndexOfArrivalIata);
            final String _tmpArrivalName;
            _tmpArrivalName = _cursor.getString(_cursorIndexOfArrivalName);
            final double _tmpArrivalLat;
            _tmpArrivalLat = _cursor.getDouble(_cursorIndexOfArrivalLat);
            final double _tmpArrivalLon;
            _tmpArrivalLon = _cursor.getDouble(_cursorIndexOfArrivalLon);
            final String _tmpArrivalTz;
            _tmpArrivalTz = _cursor.getString(_cursorIndexOfArrivalTz);
            final String _tmpAirline;
            _tmpAirline = _cursor.getString(_cursorIndexOfAirline);
            final long _tmpScheduledDepartureMs;
            _tmpScheduledDepartureMs = _cursor.getLong(_cursorIndexOfScheduledDepartureMs);
            final long _tmpScheduledArrivalMs;
            _tmpScheduledArrivalMs = _cursor.getLong(_cursorIndexOfScheduledArrivalMs);
            final long _tmpActualDepartureMs;
            _tmpActualDepartureMs = _cursor.getLong(_cursorIndexOfActualDepartureMs);
            final long _tmpActualArrivalMs;
            _tmpActualArrivalMs = _cursor.getLong(_cursorIndexOfActualArrivalMs);
            final FlightStatus _tmpStatus;
            _tmpStatus = __FlightStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final double _tmpTotalDistanceKm;
            _tmpTotalDistanceKm = _cursor.getDouble(_cursorIndexOfTotalDistanceKm);
            final double _tmpMaxAltitudeM;
            _tmpMaxAltitudeM = _cursor.getDouble(_cursorIndexOfMaxAltitudeM);
            final double _tmpMaxSpeedKmh;
            _tmpMaxSpeedKmh = _cursor.getDouble(_cursorIndexOfMaxSpeedKmh);
            final double _tmpAvgSpeedKmh;
            _tmpAvgSpeedKmh = _cursor.getDouble(_cursorIndexOfAvgSpeedKmh);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            _item = new Flight(_tmpId,_tmpDepartureIata,_tmpDepartureName,_tmpDepartureLat,_tmpDepartureLon,_tmpDepartureTz,_tmpArrivalIata,_tmpArrivalName,_tmpArrivalLat,_tmpArrivalLon,_tmpArrivalTz,_tmpAirline,_tmpScheduledDepartureMs,_tmpScheduledArrivalMs,_tmpActualDepartureMs,_tmpActualArrivalMs,_tmpStatus,_tmpTotalDistanceKm,_tmpMaxAltitudeM,_tmpMaxSpeedKmh,_tmpAvgSpeedKmh,_tmpCreatedAtMs);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Flight> getFlightById(final long id) {
    final String _sql = "SELECT * FROM flights WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flights"}, new Callable<Flight>() {
      @Override
      @Nullable
      public Flight call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDepartureIata = CursorUtil.getColumnIndexOrThrow(_cursor, "departureIata");
          final int _cursorIndexOfDepartureName = CursorUtil.getColumnIndexOrThrow(_cursor, "departureName");
          final int _cursorIndexOfDepartureLat = CursorUtil.getColumnIndexOrThrow(_cursor, "departureLat");
          final int _cursorIndexOfDepartureLon = CursorUtil.getColumnIndexOrThrow(_cursor, "departureLon");
          final int _cursorIndexOfDepartureTz = CursorUtil.getColumnIndexOrThrow(_cursor, "departureTz");
          final int _cursorIndexOfArrivalIata = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalIata");
          final int _cursorIndexOfArrivalName = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalName");
          final int _cursorIndexOfArrivalLat = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalLat");
          final int _cursorIndexOfArrivalLon = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalLon");
          final int _cursorIndexOfArrivalTz = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalTz");
          final int _cursorIndexOfAirline = CursorUtil.getColumnIndexOrThrow(_cursor, "airline");
          final int _cursorIndexOfScheduledDepartureMs = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledDepartureMs");
          final int _cursorIndexOfScheduledArrivalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledArrivalMs");
          final int _cursorIndexOfActualDepartureMs = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDepartureMs");
          final int _cursorIndexOfActualArrivalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "actualArrivalMs");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTotalDistanceKm = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistanceKm");
          final int _cursorIndexOfMaxAltitudeM = CursorUtil.getColumnIndexOrThrow(_cursor, "maxAltitudeM");
          final int _cursorIndexOfMaxSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "maxSpeedKmh");
          final int _cursorIndexOfAvgSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSpeedKmh");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final Flight _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDepartureIata;
            _tmpDepartureIata = _cursor.getString(_cursorIndexOfDepartureIata);
            final String _tmpDepartureName;
            _tmpDepartureName = _cursor.getString(_cursorIndexOfDepartureName);
            final double _tmpDepartureLat;
            _tmpDepartureLat = _cursor.getDouble(_cursorIndexOfDepartureLat);
            final double _tmpDepartureLon;
            _tmpDepartureLon = _cursor.getDouble(_cursorIndexOfDepartureLon);
            final String _tmpDepartureTz;
            _tmpDepartureTz = _cursor.getString(_cursorIndexOfDepartureTz);
            final String _tmpArrivalIata;
            _tmpArrivalIata = _cursor.getString(_cursorIndexOfArrivalIata);
            final String _tmpArrivalName;
            _tmpArrivalName = _cursor.getString(_cursorIndexOfArrivalName);
            final double _tmpArrivalLat;
            _tmpArrivalLat = _cursor.getDouble(_cursorIndexOfArrivalLat);
            final double _tmpArrivalLon;
            _tmpArrivalLon = _cursor.getDouble(_cursorIndexOfArrivalLon);
            final String _tmpArrivalTz;
            _tmpArrivalTz = _cursor.getString(_cursorIndexOfArrivalTz);
            final String _tmpAirline;
            _tmpAirline = _cursor.getString(_cursorIndexOfAirline);
            final long _tmpScheduledDepartureMs;
            _tmpScheduledDepartureMs = _cursor.getLong(_cursorIndexOfScheduledDepartureMs);
            final long _tmpScheduledArrivalMs;
            _tmpScheduledArrivalMs = _cursor.getLong(_cursorIndexOfScheduledArrivalMs);
            final long _tmpActualDepartureMs;
            _tmpActualDepartureMs = _cursor.getLong(_cursorIndexOfActualDepartureMs);
            final long _tmpActualArrivalMs;
            _tmpActualArrivalMs = _cursor.getLong(_cursorIndexOfActualArrivalMs);
            final FlightStatus _tmpStatus;
            _tmpStatus = __FlightStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final double _tmpTotalDistanceKm;
            _tmpTotalDistanceKm = _cursor.getDouble(_cursorIndexOfTotalDistanceKm);
            final double _tmpMaxAltitudeM;
            _tmpMaxAltitudeM = _cursor.getDouble(_cursorIndexOfMaxAltitudeM);
            final double _tmpMaxSpeedKmh;
            _tmpMaxSpeedKmh = _cursor.getDouble(_cursorIndexOfMaxSpeedKmh);
            final double _tmpAvgSpeedKmh;
            _tmpAvgSpeedKmh = _cursor.getDouble(_cursorIndexOfAvgSpeedKmh);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            _result = new Flight(_tmpId,_tmpDepartureIata,_tmpDepartureName,_tmpDepartureLat,_tmpDepartureLon,_tmpDepartureTz,_tmpArrivalIata,_tmpArrivalName,_tmpArrivalLat,_tmpArrivalLon,_tmpArrivalTz,_tmpAirline,_tmpScheduledDepartureMs,_tmpScheduledArrivalMs,_tmpActualDepartureMs,_tmpActualArrivalMs,_tmpStatus,_tmpTotalDistanceKm,_tmpMaxAltitudeM,_tmpMaxSpeedKmh,_tmpAvgSpeedKmh,_tmpCreatedAtMs);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getFlightByIdOnce(final long id, final Continuation<? super Flight> $completion) {
    final String _sql = "SELECT * FROM flights WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Flight>() {
      @Override
      @Nullable
      public Flight call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDepartureIata = CursorUtil.getColumnIndexOrThrow(_cursor, "departureIata");
          final int _cursorIndexOfDepartureName = CursorUtil.getColumnIndexOrThrow(_cursor, "departureName");
          final int _cursorIndexOfDepartureLat = CursorUtil.getColumnIndexOrThrow(_cursor, "departureLat");
          final int _cursorIndexOfDepartureLon = CursorUtil.getColumnIndexOrThrow(_cursor, "departureLon");
          final int _cursorIndexOfDepartureTz = CursorUtil.getColumnIndexOrThrow(_cursor, "departureTz");
          final int _cursorIndexOfArrivalIata = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalIata");
          final int _cursorIndexOfArrivalName = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalName");
          final int _cursorIndexOfArrivalLat = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalLat");
          final int _cursorIndexOfArrivalLon = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalLon");
          final int _cursorIndexOfArrivalTz = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalTz");
          final int _cursorIndexOfAirline = CursorUtil.getColumnIndexOrThrow(_cursor, "airline");
          final int _cursorIndexOfScheduledDepartureMs = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledDepartureMs");
          final int _cursorIndexOfScheduledArrivalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledArrivalMs");
          final int _cursorIndexOfActualDepartureMs = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDepartureMs");
          final int _cursorIndexOfActualArrivalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "actualArrivalMs");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTotalDistanceKm = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistanceKm");
          final int _cursorIndexOfMaxAltitudeM = CursorUtil.getColumnIndexOrThrow(_cursor, "maxAltitudeM");
          final int _cursorIndexOfMaxSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "maxSpeedKmh");
          final int _cursorIndexOfAvgSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSpeedKmh");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final Flight _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDepartureIata;
            _tmpDepartureIata = _cursor.getString(_cursorIndexOfDepartureIata);
            final String _tmpDepartureName;
            _tmpDepartureName = _cursor.getString(_cursorIndexOfDepartureName);
            final double _tmpDepartureLat;
            _tmpDepartureLat = _cursor.getDouble(_cursorIndexOfDepartureLat);
            final double _tmpDepartureLon;
            _tmpDepartureLon = _cursor.getDouble(_cursorIndexOfDepartureLon);
            final String _tmpDepartureTz;
            _tmpDepartureTz = _cursor.getString(_cursorIndexOfDepartureTz);
            final String _tmpArrivalIata;
            _tmpArrivalIata = _cursor.getString(_cursorIndexOfArrivalIata);
            final String _tmpArrivalName;
            _tmpArrivalName = _cursor.getString(_cursorIndexOfArrivalName);
            final double _tmpArrivalLat;
            _tmpArrivalLat = _cursor.getDouble(_cursorIndexOfArrivalLat);
            final double _tmpArrivalLon;
            _tmpArrivalLon = _cursor.getDouble(_cursorIndexOfArrivalLon);
            final String _tmpArrivalTz;
            _tmpArrivalTz = _cursor.getString(_cursorIndexOfArrivalTz);
            final String _tmpAirline;
            _tmpAirline = _cursor.getString(_cursorIndexOfAirline);
            final long _tmpScheduledDepartureMs;
            _tmpScheduledDepartureMs = _cursor.getLong(_cursorIndexOfScheduledDepartureMs);
            final long _tmpScheduledArrivalMs;
            _tmpScheduledArrivalMs = _cursor.getLong(_cursorIndexOfScheduledArrivalMs);
            final long _tmpActualDepartureMs;
            _tmpActualDepartureMs = _cursor.getLong(_cursorIndexOfActualDepartureMs);
            final long _tmpActualArrivalMs;
            _tmpActualArrivalMs = _cursor.getLong(_cursorIndexOfActualArrivalMs);
            final FlightStatus _tmpStatus;
            _tmpStatus = __FlightStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final double _tmpTotalDistanceKm;
            _tmpTotalDistanceKm = _cursor.getDouble(_cursorIndexOfTotalDistanceKm);
            final double _tmpMaxAltitudeM;
            _tmpMaxAltitudeM = _cursor.getDouble(_cursorIndexOfMaxAltitudeM);
            final double _tmpMaxSpeedKmh;
            _tmpMaxSpeedKmh = _cursor.getDouble(_cursorIndexOfMaxSpeedKmh);
            final double _tmpAvgSpeedKmh;
            _tmpAvgSpeedKmh = _cursor.getDouble(_cursorIndexOfAvgSpeedKmh);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            _result = new Flight(_tmpId,_tmpDepartureIata,_tmpDepartureName,_tmpDepartureLat,_tmpDepartureLon,_tmpDepartureTz,_tmpArrivalIata,_tmpArrivalName,_tmpArrivalLat,_tmpArrivalLon,_tmpArrivalTz,_tmpAirline,_tmpScheduledDepartureMs,_tmpScheduledArrivalMs,_tmpActualDepartureMs,_tmpActualArrivalMs,_tmpStatus,_tmpTotalDistanceKm,_tmpMaxAltitudeM,_tmpMaxSpeedKmh,_tmpAvgSpeedKmh,_tmpCreatedAtMs);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Flight>> getFlightsByStatus(final FlightStatus status) {
    final String _sql = "SELECT * FROM flights WHERE status = ? ORDER BY createdAtMs DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, __FlightStatus_enumToString(status));
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flights"}, new Callable<List<Flight>>() {
      @Override
      @NonNull
      public List<Flight> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDepartureIata = CursorUtil.getColumnIndexOrThrow(_cursor, "departureIata");
          final int _cursorIndexOfDepartureName = CursorUtil.getColumnIndexOrThrow(_cursor, "departureName");
          final int _cursorIndexOfDepartureLat = CursorUtil.getColumnIndexOrThrow(_cursor, "departureLat");
          final int _cursorIndexOfDepartureLon = CursorUtil.getColumnIndexOrThrow(_cursor, "departureLon");
          final int _cursorIndexOfDepartureTz = CursorUtil.getColumnIndexOrThrow(_cursor, "departureTz");
          final int _cursorIndexOfArrivalIata = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalIata");
          final int _cursorIndexOfArrivalName = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalName");
          final int _cursorIndexOfArrivalLat = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalLat");
          final int _cursorIndexOfArrivalLon = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalLon");
          final int _cursorIndexOfArrivalTz = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalTz");
          final int _cursorIndexOfAirline = CursorUtil.getColumnIndexOrThrow(_cursor, "airline");
          final int _cursorIndexOfScheduledDepartureMs = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledDepartureMs");
          final int _cursorIndexOfScheduledArrivalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledArrivalMs");
          final int _cursorIndexOfActualDepartureMs = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDepartureMs");
          final int _cursorIndexOfActualArrivalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "actualArrivalMs");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTotalDistanceKm = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistanceKm");
          final int _cursorIndexOfMaxAltitudeM = CursorUtil.getColumnIndexOrThrow(_cursor, "maxAltitudeM");
          final int _cursorIndexOfMaxSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "maxSpeedKmh");
          final int _cursorIndexOfAvgSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSpeedKmh");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final List<Flight> _result = new ArrayList<Flight>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Flight _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDepartureIata;
            _tmpDepartureIata = _cursor.getString(_cursorIndexOfDepartureIata);
            final String _tmpDepartureName;
            _tmpDepartureName = _cursor.getString(_cursorIndexOfDepartureName);
            final double _tmpDepartureLat;
            _tmpDepartureLat = _cursor.getDouble(_cursorIndexOfDepartureLat);
            final double _tmpDepartureLon;
            _tmpDepartureLon = _cursor.getDouble(_cursorIndexOfDepartureLon);
            final String _tmpDepartureTz;
            _tmpDepartureTz = _cursor.getString(_cursorIndexOfDepartureTz);
            final String _tmpArrivalIata;
            _tmpArrivalIata = _cursor.getString(_cursorIndexOfArrivalIata);
            final String _tmpArrivalName;
            _tmpArrivalName = _cursor.getString(_cursorIndexOfArrivalName);
            final double _tmpArrivalLat;
            _tmpArrivalLat = _cursor.getDouble(_cursorIndexOfArrivalLat);
            final double _tmpArrivalLon;
            _tmpArrivalLon = _cursor.getDouble(_cursorIndexOfArrivalLon);
            final String _tmpArrivalTz;
            _tmpArrivalTz = _cursor.getString(_cursorIndexOfArrivalTz);
            final String _tmpAirline;
            _tmpAirline = _cursor.getString(_cursorIndexOfAirline);
            final long _tmpScheduledDepartureMs;
            _tmpScheduledDepartureMs = _cursor.getLong(_cursorIndexOfScheduledDepartureMs);
            final long _tmpScheduledArrivalMs;
            _tmpScheduledArrivalMs = _cursor.getLong(_cursorIndexOfScheduledArrivalMs);
            final long _tmpActualDepartureMs;
            _tmpActualDepartureMs = _cursor.getLong(_cursorIndexOfActualDepartureMs);
            final long _tmpActualArrivalMs;
            _tmpActualArrivalMs = _cursor.getLong(_cursorIndexOfActualArrivalMs);
            final FlightStatus _tmpStatus;
            _tmpStatus = __FlightStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final double _tmpTotalDistanceKm;
            _tmpTotalDistanceKm = _cursor.getDouble(_cursorIndexOfTotalDistanceKm);
            final double _tmpMaxAltitudeM;
            _tmpMaxAltitudeM = _cursor.getDouble(_cursorIndexOfMaxAltitudeM);
            final double _tmpMaxSpeedKmh;
            _tmpMaxSpeedKmh = _cursor.getDouble(_cursorIndexOfMaxSpeedKmh);
            final double _tmpAvgSpeedKmh;
            _tmpAvgSpeedKmh = _cursor.getDouble(_cursorIndexOfAvgSpeedKmh);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            _item = new Flight(_tmpId,_tmpDepartureIata,_tmpDepartureName,_tmpDepartureLat,_tmpDepartureLon,_tmpDepartureTz,_tmpArrivalIata,_tmpArrivalName,_tmpArrivalLat,_tmpArrivalLon,_tmpArrivalTz,_tmpAirline,_tmpScheduledDepartureMs,_tmpScheduledArrivalMs,_tmpActualDepartureMs,_tmpActualArrivalMs,_tmpStatus,_tmpTotalDistanceKm,_tmpMaxAltitudeM,_tmpMaxSpeedKmh,_tmpAvgSpeedKmh,_tmpCreatedAtMs);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Flight> getActiveFlight() {
    final String _sql = "\n"
            + "        SELECT * FROM flights\n"
            + "        WHERE status IN ('AIRBORNE', 'DESCENDING', 'BOARDING')\n"
            + "        ORDER BY createdAtMs DESC\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flights"}, new Callable<Flight>() {
      @Override
      @Nullable
      public Flight call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDepartureIata = CursorUtil.getColumnIndexOrThrow(_cursor, "departureIata");
          final int _cursorIndexOfDepartureName = CursorUtil.getColumnIndexOrThrow(_cursor, "departureName");
          final int _cursorIndexOfDepartureLat = CursorUtil.getColumnIndexOrThrow(_cursor, "departureLat");
          final int _cursorIndexOfDepartureLon = CursorUtil.getColumnIndexOrThrow(_cursor, "departureLon");
          final int _cursorIndexOfDepartureTz = CursorUtil.getColumnIndexOrThrow(_cursor, "departureTz");
          final int _cursorIndexOfArrivalIata = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalIata");
          final int _cursorIndexOfArrivalName = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalName");
          final int _cursorIndexOfArrivalLat = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalLat");
          final int _cursorIndexOfArrivalLon = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalLon");
          final int _cursorIndexOfArrivalTz = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalTz");
          final int _cursorIndexOfAirline = CursorUtil.getColumnIndexOrThrow(_cursor, "airline");
          final int _cursorIndexOfScheduledDepartureMs = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledDepartureMs");
          final int _cursorIndexOfScheduledArrivalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledArrivalMs");
          final int _cursorIndexOfActualDepartureMs = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDepartureMs");
          final int _cursorIndexOfActualArrivalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "actualArrivalMs");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTotalDistanceKm = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistanceKm");
          final int _cursorIndexOfMaxAltitudeM = CursorUtil.getColumnIndexOrThrow(_cursor, "maxAltitudeM");
          final int _cursorIndexOfMaxSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "maxSpeedKmh");
          final int _cursorIndexOfAvgSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSpeedKmh");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final Flight _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDepartureIata;
            _tmpDepartureIata = _cursor.getString(_cursorIndexOfDepartureIata);
            final String _tmpDepartureName;
            _tmpDepartureName = _cursor.getString(_cursorIndexOfDepartureName);
            final double _tmpDepartureLat;
            _tmpDepartureLat = _cursor.getDouble(_cursorIndexOfDepartureLat);
            final double _tmpDepartureLon;
            _tmpDepartureLon = _cursor.getDouble(_cursorIndexOfDepartureLon);
            final String _tmpDepartureTz;
            _tmpDepartureTz = _cursor.getString(_cursorIndexOfDepartureTz);
            final String _tmpArrivalIata;
            _tmpArrivalIata = _cursor.getString(_cursorIndexOfArrivalIata);
            final String _tmpArrivalName;
            _tmpArrivalName = _cursor.getString(_cursorIndexOfArrivalName);
            final double _tmpArrivalLat;
            _tmpArrivalLat = _cursor.getDouble(_cursorIndexOfArrivalLat);
            final double _tmpArrivalLon;
            _tmpArrivalLon = _cursor.getDouble(_cursorIndexOfArrivalLon);
            final String _tmpArrivalTz;
            _tmpArrivalTz = _cursor.getString(_cursorIndexOfArrivalTz);
            final String _tmpAirline;
            _tmpAirline = _cursor.getString(_cursorIndexOfAirline);
            final long _tmpScheduledDepartureMs;
            _tmpScheduledDepartureMs = _cursor.getLong(_cursorIndexOfScheduledDepartureMs);
            final long _tmpScheduledArrivalMs;
            _tmpScheduledArrivalMs = _cursor.getLong(_cursorIndexOfScheduledArrivalMs);
            final long _tmpActualDepartureMs;
            _tmpActualDepartureMs = _cursor.getLong(_cursorIndexOfActualDepartureMs);
            final long _tmpActualArrivalMs;
            _tmpActualArrivalMs = _cursor.getLong(_cursorIndexOfActualArrivalMs);
            final FlightStatus _tmpStatus;
            _tmpStatus = __FlightStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final double _tmpTotalDistanceKm;
            _tmpTotalDistanceKm = _cursor.getDouble(_cursorIndexOfTotalDistanceKm);
            final double _tmpMaxAltitudeM;
            _tmpMaxAltitudeM = _cursor.getDouble(_cursorIndexOfMaxAltitudeM);
            final double _tmpMaxSpeedKmh;
            _tmpMaxSpeedKmh = _cursor.getDouble(_cursorIndexOfMaxSpeedKmh);
            final double _tmpAvgSpeedKmh;
            _tmpAvgSpeedKmh = _cursor.getDouble(_cursorIndexOfAvgSpeedKmh);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            _result = new Flight(_tmpId,_tmpDepartureIata,_tmpDepartureName,_tmpDepartureLat,_tmpDepartureLon,_tmpDepartureTz,_tmpArrivalIata,_tmpArrivalName,_tmpArrivalLat,_tmpArrivalLon,_tmpArrivalTz,_tmpAirline,_tmpScheduledDepartureMs,_tmpScheduledArrivalMs,_tmpActualDepartureMs,_tmpActualArrivalMs,_tmpStatus,_tmpTotalDistanceKm,_tmpMaxAltitudeM,_tmpMaxSpeedKmh,_tmpAvgSpeedKmh,_tmpCreatedAtMs);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Flight>> getCompletedFlights() {
    final String _sql = "\n"
            + "        SELECT * FROM flights\n"
            + "        WHERE status = 'COMPLETED'\n"
            + "        ORDER BY createdAtMs DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flights"}, new Callable<List<Flight>>() {
      @Override
      @NonNull
      public List<Flight> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDepartureIata = CursorUtil.getColumnIndexOrThrow(_cursor, "departureIata");
          final int _cursorIndexOfDepartureName = CursorUtil.getColumnIndexOrThrow(_cursor, "departureName");
          final int _cursorIndexOfDepartureLat = CursorUtil.getColumnIndexOrThrow(_cursor, "departureLat");
          final int _cursorIndexOfDepartureLon = CursorUtil.getColumnIndexOrThrow(_cursor, "departureLon");
          final int _cursorIndexOfDepartureTz = CursorUtil.getColumnIndexOrThrow(_cursor, "departureTz");
          final int _cursorIndexOfArrivalIata = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalIata");
          final int _cursorIndexOfArrivalName = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalName");
          final int _cursorIndexOfArrivalLat = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalLat");
          final int _cursorIndexOfArrivalLon = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalLon");
          final int _cursorIndexOfArrivalTz = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalTz");
          final int _cursorIndexOfAirline = CursorUtil.getColumnIndexOrThrow(_cursor, "airline");
          final int _cursorIndexOfScheduledDepartureMs = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledDepartureMs");
          final int _cursorIndexOfScheduledArrivalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledArrivalMs");
          final int _cursorIndexOfActualDepartureMs = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDepartureMs");
          final int _cursorIndexOfActualArrivalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "actualArrivalMs");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTotalDistanceKm = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistanceKm");
          final int _cursorIndexOfMaxAltitudeM = CursorUtil.getColumnIndexOrThrow(_cursor, "maxAltitudeM");
          final int _cursorIndexOfMaxSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "maxSpeedKmh");
          final int _cursorIndexOfAvgSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSpeedKmh");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final List<Flight> _result = new ArrayList<Flight>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Flight _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDepartureIata;
            _tmpDepartureIata = _cursor.getString(_cursorIndexOfDepartureIata);
            final String _tmpDepartureName;
            _tmpDepartureName = _cursor.getString(_cursorIndexOfDepartureName);
            final double _tmpDepartureLat;
            _tmpDepartureLat = _cursor.getDouble(_cursorIndexOfDepartureLat);
            final double _tmpDepartureLon;
            _tmpDepartureLon = _cursor.getDouble(_cursorIndexOfDepartureLon);
            final String _tmpDepartureTz;
            _tmpDepartureTz = _cursor.getString(_cursorIndexOfDepartureTz);
            final String _tmpArrivalIata;
            _tmpArrivalIata = _cursor.getString(_cursorIndexOfArrivalIata);
            final String _tmpArrivalName;
            _tmpArrivalName = _cursor.getString(_cursorIndexOfArrivalName);
            final double _tmpArrivalLat;
            _tmpArrivalLat = _cursor.getDouble(_cursorIndexOfArrivalLat);
            final double _tmpArrivalLon;
            _tmpArrivalLon = _cursor.getDouble(_cursorIndexOfArrivalLon);
            final String _tmpArrivalTz;
            _tmpArrivalTz = _cursor.getString(_cursorIndexOfArrivalTz);
            final String _tmpAirline;
            _tmpAirline = _cursor.getString(_cursorIndexOfAirline);
            final long _tmpScheduledDepartureMs;
            _tmpScheduledDepartureMs = _cursor.getLong(_cursorIndexOfScheduledDepartureMs);
            final long _tmpScheduledArrivalMs;
            _tmpScheduledArrivalMs = _cursor.getLong(_cursorIndexOfScheduledArrivalMs);
            final long _tmpActualDepartureMs;
            _tmpActualDepartureMs = _cursor.getLong(_cursorIndexOfActualDepartureMs);
            final long _tmpActualArrivalMs;
            _tmpActualArrivalMs = _cursor.getLong(_cursorIndexOfActualArrivalMs);
            final FlightStatus _tmpStatus;
            _tmpStatus = __FlightStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final double _tmpTotalDistanceKm;
            _tmpTotalDistanceKm = _cursor.getDouble(_cursorIndexOfTotalDistanceKm);
            final double _tmpMaxAltitudeM;
            _tmpMaxAltitudeM = _cursor.getDouble(_cursorIndexOfMaxAltitudeM);
            final double _tmpMaxSpeedKmh;
            _tmpMaxSpeedKmh = _cursor.getDouble(_cursorIndexOfMaxSpeedKmh);
            final double _tmpAvgSpeedKmh;
            _tmpAvgSpeedKmh = _cursor.getDouble(_cursorIndexOfAvgSpeedKmh);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            _item = new Flight(_tmpId,_tmpDepartureIata,_tmpDepartureName,_tmpDepartureLat,_tmpDepartureLon,_tmpDepartureTz,_tmpArrivalIata,_tmpArrivalName,_tmpArrivalLat,_tmpArrivalLon,_tmpArrivalTz,_tmpAirline,_tmpScheduledDepartureMs,_tmpScheduledArrivalMs,_tmpActualDepartureMs,_tmpActualArrivalMs,_tmpStatus,_tmpTotalDistanceKm,_tmpMaxAltitudeM,_tmpMaxSpeedKmh,_tmpAvgSpeedKmh,_tmpCreatedAtMs);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getCompletedFlightCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM flights WHERE status = 'COMPLETED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalDistanceKm(final Continuation<? super Double> $completion) {
    final String _sql = "\n"
            + "        SELECT SUM(totalDistanceKm) FROM flights\n"
            + "        WHERE status = 'COMPLETED'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalFlightTimeMs(final Continuation<? super Long> $completion) {
    final String _sql = "\n"
            + "        SELECT SUM(actualArrivalMs - actualDepartureMs) FROM flights\n"
            + "        WHERE status = 'COMPLETED' AND actualArrivalMs > 0\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPersonalAltitudeRecord(final Continuation<? super Double> $completion) {
    final String _sql = "\n"
            + "        SELECT MAX(maxAltitudeM) FROM flights\n"
            + "        WHERE status = 'COMPLETED'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPersonalSpeedRecord(final Continuation<? super Double> $completion) {
    final String _sql = "\n"
            + "        SELECT MAX(maxSpeedKmh) FROM flights\n"
            + "        WHERE status = 'COMPLETED'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __FlightStatus_enumToString(@NonNull final FlightStatus _value) {
    switch (_value) {
      case SETUP: return "SETUP";
      case BOARDING: return "BOARDING";
      case AIRBORNE: return "AIRBORNE";
      case DESCENDING: return "DESCENDING";
      case LANDED: return "LANDED";
      case COMPLETED: return "COMPLETED";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private FlightStatus __FlightStatus_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "SETUP": return FlightStatus.SETUP;
      case "BOARDING": return FlightStatus.BOARDING;
      case "AIRBORNE": return FlightStatus.AIRBORNE;
      case "DESCENDING": return FlightStatus.DESCENDING;
      case "LANDED": return FlightStatus.LANDED;
      case "COMPLETED": return FlightStatus.COMPLETED;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
