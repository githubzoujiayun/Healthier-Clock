package com.jkydjk.healthier.clock;

import com.jkydjk.healthier.clock.database.AlarmDatabaseHelper;
import com.jkydjk.healthier.clock.entity.columns.AlarmColumns;
import com.jkydjk.healthier.clock.util.Log;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class AlarmProvider extends ContentProvider {
  
  private SQLiteOpenHelper alarmDatabaseOpenHelper;

  private static final int ALARMS = 1;
  private static final int ALARMS_ID = 2;
  private static final UriMatcher sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    sURLMatcher.addURI("com.jkydjk.healthier.clock", "alarm", ALARMS);
    sURLMatcher.addURI("com.jkydjk.healthier.clock", "alarm/#", ALARMS_ID);
  }

  public AlarmProvider() {
  }

  @Override
  public boolean onCreate() {
    alarmDatabaseOpenHelper = new AlarmDatabaseHelper(getContext());
    return true;
  }

  @Override
  public Cursor query(Uri url, String[] projectionIn, String selection, String[] selectionArgs, String sort) {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

    // Generate the body of the query
    int match = sURLMatcher.match(url);
    switch (match) {
    case ALARMS:
      qb.setTables("alarms");
      break;
    case ALARMS_ID:
      qb.setTables("alarms");
      qb.appendWhere("_id=");
      qb.appendWhere(url.getPathSegments().get(1));
      break;
    default:
      throw new IllegalArgumentException("Unknown URL " + url);
    }

    SQLiteDatabase db = alarmDatabaseOpenHelper.getReadableDatabase();
    Cursor ret = qb.query(db, projectionIn, selection, selectionArgs, null, null, sort);

    if (ret == null) {
      if (Log.LOGV)
        Log.v("Alarms.query: failed");
    } else {
      ret.setNotificationUri(getContext().getContentResolver(), url);
    }

    return ret;
  }

  @Override
  public String getType(Uri url) {
    int match = sURLMatcher.match(url);
    switch (match) {
    case ALARMS:
      return "vnd.android.cursor.dir/alarms";
    case ALARMS_ID:
      return "vnd.android.cursor.item/alarms";
    default:
      throw new IllegalArgumentException("Unknown URL");
    }
  }

  @Override
  public int update(Uri url, ContentValues values, String where, String[] whereArgs) {
    int count;
    long rowId = 0;
    int match = sURLMatcher.match(url);
    SQLiteDatabase db = alarmDatabaseOpenHelper.getWritableDatabase();
    switch (match) {
    case ALARMS_ID: {
      String segment = url.getPathSegments().get(1);
      rowId = Long.parseLong(segment);
      count = db.update("alarms", values, "_id=" + rowId, null);
      break;
    }
    default: {
      throw new UnsupportedOperationException("Cannot update URL: " + url);
    }
    }
    if (Log.LOGV)
      Log.v("*** notifyChange() rowId: " + rowId + " url " + url);
    getContext().getContentResolver().notifyChange(url, null);
    return count;
  }

  @Override
  public Uri insert(Uri url, ContentValues initialValues) {
    if (sURLMatcher.match(url) != ALARMS) {
      throw new IllegalArgumentException("Cannot insert into URL: " + url);
    }

    ContentValues values;
    if (initialValues != null)
      values = new ContentValues(initialValues);
    else
      values = new ContentValues();

    if (!values.containsKey(AlarmColumns.HOUR))
      values.put(AlarmColumns.HOUR, 0);

    if (!values.containsKey(AlarmColumns.MINUTES))
      values.put(AlarmColumns.MINUTES, 0);

    if (!values.containsKey(AlarmColumns.DAYS_OF_WEEK))
      values.put(AlarmColumns.DAYS_OF_WEEK, 0);

    if (!values.containsKey(AlarmColumns.ALARM_TIME))
      values.put(AlarmColumns.ALARM_TIME, 0);

    if (!values.containsKey(AlarmColumns.ENABLED))
      values.put(AlarmColumns.ENABLED, 0);

    if (!values.containsKey(AlarmColumns.VIBRATE))
      values.put(AlarmColumns.VIBRATE, 1);

    if (!values.containsKey(AlarmColumns.REMARK))
      values.put(AlarmColumns.REMARK, "");

    if (!values.containsKey(AlarmColumns.ALERT))
      values.put(AlarmColumns.ALERT, "");

    SQLiteDatabase db = alarmDatabaseOpenHelper.getWritableDatabase();
    long rowId = db.insert("alarms", AlarmColumns.REMARK, values);
    if (rowId < 0) {
      throw new SQLException("Failed to insert row into " + url);
    }
    if (Log.LOGV)
      Log.v("Added alarm rowId = " + rowId);

    Uri newUrl = ContentUris.withAppendedId(AlarmColumns.CONTENT_URI, rowId);
    getContext().getContentResolver().notifyChange(newUrl, null);
    return newUrl;
  }

  public int delete(Uri url, String where, String[] whereArgs) {
    SQLiteDatabase db = alarmDatabaseOpenHelper.getWritableDatabase();
    int count;
    long rowId = 0;
    switch (sURLMatcher.match(url)) {
    case ALARMS:
      count = db.delete("alarms", where, whereArgs);
      break;
    case ALARMS_ID:
      String segment = url.getPathSegments().get(1);
      rowId = Long.parseLong(segment);
      if (TextUtils.isEmpty(where)) {
        where = "_id=" + segment;
      } else {
        where = "_id=" + segment + " AND (" + where + ")";
      }
      count = db.delete("alarms", where, whereArgs);
      break;
    default:
      throw new IllegalArgumentException("Cannot delete from URL: " + url);
    }

    getContext().getContentResolver().notifyChange(url, null);
    return count;
  }
}
