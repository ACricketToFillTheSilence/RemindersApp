package com.reminders.android.remindersapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper for storing stuff locally.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String PROMISE_TABLE_NAME = "PromiseTable";

    public static int ACTION_ACCEPTED = 1;
    public static int ACTION_SKIPPED = 2;
    public static int ACTION_COMPLETED = 3;
    public static int ACTION_NOT_COMPLETED_FORGOT = 4;
    public static int ACTION_NOT_COMPLETED_NO_OPPORTUNITY = 5;

    private static class PromiseColumns {
        public static String TIMESTAMP = "timestamp";
        public static String PROMISE_ID = "promiseid";
        public static String ACTION = "action";
    }

    private static final String PROMISE_TABLE_CREATE = "CREATE TABLE " + PROMISE_TABLE_NAME +
            " (" + PromiseColumns.TIMESTAMP + " LONG, " + PromiseColumns.PROMISE_ID + " STRING, " +
            PromiseColumns.ACTION + " INTEGER);";

    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, PROMISE_TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PROMISE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void logAction(long timestamp, String promiseId, int action) {
        ContentValues values = new ContentValues();
        values.put(PromiseColumns.TIMESTAMP, timestamp);
        values.put(PromiseColumns.PROMISE_ID, promiseId);
        values.put(PromiseColumns.ACTION, action);
        getWritableDatabase().insertWithOnConflict(PROMISE_TABLE_NAME, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);
    }

    public boolean containsPromise(String promiseId) {
        Cursor c = getReadableDatabase().rawQuery("SELECT " + PromiseColumns.ACTION + " FROM " +
                PROMISE_TABLE_NAME + " WHERE " + PromiseColumns.PROMISE_ID + " = " + promiseId +
                " LIMIT 1", null);
        boolean result = c.getCount() > 0;
        c.close();
        return result;
    }
}
