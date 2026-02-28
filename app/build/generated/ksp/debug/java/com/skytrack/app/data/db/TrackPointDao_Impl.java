package com.skytrack.app.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.skytrack.app.data.model.TrackPoint;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class TrackPointDao_Impl implements TrackPointDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TrackPoint> __insertionAdapterOfTrackPoint;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByFlightId;

  public TrackPointDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTrackPoint = new EntityInsertionAdapter<TrackPoint>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `track_points` (`id`,`flightId`,`lat`,`lon`,`altitudeM`,`speedKmh`,`heading`,`accuracy`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TrackPoint entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getFlightId());
        statement.bindDouble(3, entity.getLat());
        statement.bindDouble(4, entity.getLon());
        statement.bindDouble(5, entity.getAltitudeM());
        statement.bindDouble(6, entity.getSpeedKmh());
        statement.bindDouble(7, entity.getHeading());
        statement.bindDouble(8, entity.getAccuracy());
        statement.bindLong(9, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfDeleteByFlightId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM track_points WHERE flightId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final TrackPoint trackPoint, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTrackPoint.insert(trackPoint);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<TrackPoint> trackPoints,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTrackPoint.insert(trackPoints);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByFlightId(final long flightId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByFlightId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, flightId);
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
          __preparedStmtOfDeleteByFlightId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TrackPoint>> getTrackPointsForFlight(final long flightId) {
    final String _sql = "SELECT * FROM track_points WHERE flightId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, flightId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"track_points"}, new Callable<List<TrackPoint>>() {
      @Override
      @NonNull
      public List<TrackPoint> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFlightId = CursorUtil.getColumnIndexOrThrow(_cursor, "flightId");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLon = CursorUtil.getColumnIndexOrThrow(_cursor, "lon");
          final int _cursorIndexOfAltitudeM = CursorUtil.getColumnIndexOrThrow(_cursor, "altitudeM");
          final int _cursorIndexOfSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "speedKmh");
          final int _cursorIndexOfHeading = CursorUtil.getColumnIndexOrThrow(_cursor, "heading");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<TrackPoint> _result = new ArrayList<TrackPoint>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrackPoint _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFlightId;
            _tmpFlightId = _cursor.getLong(_cursorIndexOfFlightId);
            final double _tmpLat;
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
            final double _tmpLon;
            _tmpLon = _cursor.getDouble(_cursorIndexOfLon);
            final double _tmpAltitudeM;
            _tmpAltitudeM = _cursor.getDouble(_cursorIndexOfAltitudeM);
            final double _tmpSpeedKmh;
            _tmpSpeedKmh = _cursor.getDouble(_cursorIndexOfSpeedKmh);
            final float _tmpHeading;
            _tmpHeading = _cursor.getFloat(_cursorIndexOfHeading);
            final float _tmpAccuracy;
            _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new TrackPoint(_tmpId,_tmpFlightId,_tmpLat,_tmpLon,_tmpAltitudeM,_tmpSpeedKmh,_tmpHeading,_tmpAccuracy,_tmpTimestamp);
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
  public Object getTrackPointsForFlightOnce(final long flightId,
      final Continuation<? super List<TrackPoint>> $completion) {
    final String _sql = "SELECT * FROM track_points WHERE flightId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, flightId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TrackPoint>>() {
      @Override
      @NonNull
      public List<TrackPoint> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFlightId = CursorUtil.getColumnIndexOrThrow(_cursor, "flightId");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLon = CursorUtil.getColumnIndexOrThrow(_cursor, "lon");
          final int _cursorIndexOfAltitudeM = CursorUtil.getColumnIndexOrThrow(_cursor, "altitudeM");
          final int _cursorIndexOfSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "speedKmh");
          final int _cursorIndexOfHeading = CursorUtil.getColumnIndexOrThrow(_cursor, "heading");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<TrackPoint> _result = new ArrayList<TrackPoint>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrackPoint _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFlightId;
            _tmpFlightId = _cursor.getLong(_cursorIndexOfFlightId);
            final double _tmpLat;
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
            final double _tmpLon;
            _tmpLon = _cursor.getDouble(_cursorIndexOfLon);
            final double _tmpAltitudeM;
            _tmpAltitudeM = _cursor.getDouble(_cursorIndexOfAltitudeM);
            final double _tmpSpeedKmh;
            _tmpSpeedKmh = _cursor.getDouble(_cursorIndexOfSpeedKmh);
            final float _tmpHeading;
            _tmpHeading = _cursor.getFloat(_cursorIndexOfHeading);
            final float _tmpAccuracy;
            _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new TrackPoint(_tmpId,_tmpFlightId,_tmpLat,_tmpLon,_tmpAltitudeM,_tmpSpeedKmh,_tmpHeading,_tmpAccuracy,_tmpTimestamp);
            _result.add(_item);
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
  public Object getTrackPointCount(final long flightId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM track_points WHERE flightId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, flightId);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
