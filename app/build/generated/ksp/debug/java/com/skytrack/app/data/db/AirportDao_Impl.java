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
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.skytrack.app.data.model.Airport;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class AirportDao_Impl implements AirportDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Airport> __insertionAdapterOfAirport;

  private final EntityDeletionOrUpdateAdapter<Airport> __deletionAdapterOfAirport;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public AirportDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAirport = new EntityInsertionAdapter<Airport>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `airports` (`iata`,`icao`,`name`,`city`,`country`,`lat`,`lon`,`tz`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Airport entity) {
        statement.bindString(1, entity.getIata());
        statement.bindString(2, entity.getIcao());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getCity());
        statement.bindString(5, entity.getCountry());
        statement.bindDouble(6, entity.getLat());
        statement.bindDouble(7, entity.getLon());
        statement.bindString(8, entity.getTz());
      }
    };
    this.__deletionAdapterOfAirport = new EntityDeletionOrUpdateAdapter<Airport>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `airports` WHERE `iata` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Airport entity) {
        statement.bindString(1, entity.getIata());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM airports";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<Airport> airports,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAirport.insert(airports);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insert(final Airport airport, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAirport.insert(airport);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Airport airport, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAirport.handle(airport);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
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
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Airport>> getAllAirports() {
    final String _sql = "SELECT * FROM airports ORDER BY iata ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"airports"}, new Callable<List<Airport>>() {
      @Override
      @NonNull
      public List<Airport> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIata = CursorUtil.getColumnIndexOrThrow(_cursor, "iata");
          final int _cursorIndexOfIcao = CursorUtil.getColumnIndexOrThrow(_cursor, "icao");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(_cursor, "country");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLon = CursorUtil.getColumnIndexOrThrow(_cursor, "lon");
          final int _cursorIndexOfTz = CursorUtil.getColumnIndexOrThrow(_cursor, "tz");
          final List<Airport> _result = new ArrayList<Airport>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Airport _item;
            final String _tmpIata;
            _tmpIata = _cursor.getString(_cursorIndexOfIata);
            final String _tmpIcao;
            _tmpIcao = _cursor.getString(_cursorIndexOfIcao);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCity;
            _tmpCity = _cursor.getString(_cursorIndexOfCity);
            final String _tmpCountry;
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry);
            final double _tmpLat;
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
            final double _tmpLon;
            _tmpLon = _cursor.getDouble(_cursorIndexOfLon);
            final String _tmpTz;
            _tmpTz = _cursor.getString(_cursorIndexOfTz);
            _item = new Airport(_tmpIata,_tmpIcao,_tmpName,_tmpCity,_tmpCountry,_tmpLat,_tmpLon,_tmpTz);
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
  public Object getAirportByIata(final String iata,
      final Continuation<? super Airport> $completion) {
    final String _sql = "SELECT * FROM airports WHERE iata = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, iata);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Airport>() {
      @Override
      @Nullable
      public Airport call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIata = CursorUtil.getColumnIndexOrThrow(_cursor, "iata");
          final int _cursorIndexOfIcao = CursorUtil.getColumnIndexOrThrow(_cursor, "icao");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(_cursor, "country");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLon = CursorUtil.getColumnIndexOrThrow(_cursor, "lon");
          final int _cursorIndexOfTz = CursorUtil.getColumnIndexOrThrow(_cursor, "tz");
          final Airport _result;
          if (_cursor.moveToFirst()) {
            final String _tmpIata;
            _tmpIata = _cursor.getString(_cursorIndexOfIata);
            final String _tmpIcao;
            _tmpIcao = _cursor.getString(_cursorIndexOfIcao);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCity;
            _tmpCity = _cursor.getString(_cursorIndexOfCity);
            final String _tmpCountry;
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry);
            final double _tmpLat;
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
            final double _tmpLon;
            _tmpLon = _cursor.getDouble(_cursorIndexOfLon);
            final String _tmpTz;
            _tmpTz = _cursor.getString(_cursorIndexOfTz);
            _result = new Airport(_tmpIata,_tmpIcao,_tmpName,_tmpCity,_tmpCountry,_tmpLat,_tmpLon,_tmpTz);
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
  public Object getAirportByIcao(final String icao,
      final Continuation<? super Airport> $completion) {
    final String _sql = "SELECT * FROM airports WHERE icao = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, icao);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Airport>() {
      @Override
      @Nullable
      public Airport call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIata = CursorUtil.getColumnIndexOrThrow(_cursor, "iata");
          final int _cursorIndexOfIcao = CursorUtil.getColumnIndexOrThrow(_cursor, "icao");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(_cursor, "country");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLon = CursorUtil.getColumnIndexOrThrow(_cursor, "lon");
          final int _cursorIndexOfTz = CursorUtil.getColumnIndexOrThrow(_cursor, "tz");
          final Airport _result;
          if (_cursor.moveToFirst()) {
            final String _tmpIata;
            _tmpIata = _cursor.getString(_cursorIndexOfIata);
            final String _tmpIcao;
            _tmpIcao = _cursor.getString(_cursorIndexOfIcao);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCity;
            _tmpCity = _cursor.getString(_cursorIndexOfCity);
            final String _tmpCountry;
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry);
            final double _tmpLat;
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
            final double _tmpLon;
            _tmpLon = _cursor.getDouble(_cursorIndexOfLon);
            final String _tmpTz;
            _tmpTz = _cursor.getString(_cursorIndexOfTz);
            _result = new Airport(_tmpIata,_tmpIcao,_tmpName,_tmpCity,_tmpCountry,_tmpLat,_tmpLon,_tmpTz);
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
  public Object searchAirports(final String query, final String exactQuery,
      final Continuation<? super List<Airport>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM airports\n"
            + "        WHERE iata LIKE ?\n"
            + "           OR icao LIKE ?\n"
            + "           OR name LIKE ?\n"
            + "           OR city LIKE ?\n"
            + "        ORDER BY\n"
            + "            CASE WHEN iata = ? THEN 0\n"
            + "                 WHEN iata LIKE ? THEN 1\n"
            + "                 WHEN city LIKE ? THEN 2\n"
            + "                 ELSE 3 END,\n"
            + "            name ASC\n"
            + "        LIMIT 50\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 7);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    _argIndex = 3;
    _statement.bindString(_argIndex, query);
    _argIndex = 4;
    _statement.bindString(_argIndex, query);
    _argIndex = 5;
    _statement.bindString(_argIndex, exactQuery);
    _argIndex = 6;
    _statement.bindString(_argIndex, query);
    _argIndex = 7;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Airport>>() {
      @Override
      @NonNull
      public List<Airport> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIata = CursorUtil.getColumnIndexOrThrow(_cursor, "iata");
          final int _cursorIndexOfIcao = CursorUtil.getColumnIndexOrThrow(_cursor, "icao");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(_cursor, "country");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLon = CursorUtil.getColumnIndexOrThrow(_cursor, "lon");
          final int _cursorIndexOfTz = CursorUtil.getColumnIndexOrThrow(_cursor, "tz");
          final List<Airport> _result = new ArrayList<Airport>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Airport _item;
            final String _tmpIata;
            _tmpIata = _cursor.getString(_cursorIndexOfIata);
            final String _tmpIcao;
            _tmpIcao = _cursor.getString(_cursorIndexOfIcao);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCity;
            _tmpCity = _cursor.getString(_cursorIndexOfCity);
            final String _tmpCountry;
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry);
            final double _tmpLat;
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
            final double _tmpLon;
            _tmpLon = _cursor.getDouble(_cursorIndexOfLon);
            final String _tmpTz;
            _tmpTz = _cursor.getString(_cursorIndexOfTz);
            _item = new Airport(_tmpIata,_tmpIcao,_tmpName,_tmpCity,_tmpCountry,_tmpLat,_tmpLon,_tmpTz);
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
  public Object getAirportCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM airports";
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
  public Object getAirportsByIataList(final List<String> iataList,
      final Continuation<? super List<Airport>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM airports");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE iata IN (");
    final int _inputSize = iataList.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ORDER BY name ASC");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : iataList) {
      _statement.bindString(_argIndex, _item);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Airport>>() {
      @Override
      @NonNull
      public List<Airport> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIata = CursorUtil.getColumnIndexOrThrow(_cursor, "iata");
          final int _cursorIndexOfIcao = CursorUtil.getColumnIndexOrThrow(_cursor, "icao");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(_cursor, "country");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLon = CursorUtil.getColumnIndexOrThrow(_cursor, "lon");
          final int _cursorIndexOfTz = CursorUtil.getColumnIndexOrThrow(_cursor, "tz");
          final List<Airport> _result = new ArrayList<Airport>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Airport _item_1;
            final String _tmpIata;
            _tmpIata = _cursor.getString(_cursorIndexOfIata);
            final String _tmpIcao;
            _tmpIcao = _cursor.getString(_cursorIndexOfIcao);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCity;
            _tmpCity = _cursor.getString(_cursorIndexOfCity);
            final String _tmpCountry;
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry);
            final double _tmpLat;
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
            final double _tmpLon;
            _tmpLon = _cursor.getDouble(_cursorIndexOfLon);
            final String _tmpTz;
            _tmpTz = _cursor.getString(_cursorIndexOfTz);
            _item_1 = new Airport(_tmpIata,_tmpIcao,_tmpName,_tmpCity,_tmpCountry,_tmpLat,_tmpLon,_tmpTz);
            _result.add(_item_1);
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
