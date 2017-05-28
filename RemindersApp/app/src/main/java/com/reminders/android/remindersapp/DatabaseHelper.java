package com.reminders.android.remindersapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * DatabaseHelper for storing stuff locally.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String PROMISE_ACTIONS_TABLE_NAME = "PromiseActionTable";
    private static final String PROMISE_TABLE_NAME = "PromiseTable";

    public static int ACTION_ACCEPTED = 1;
    public static int ACTION_SKIPPED = 2;
    public static int ACTION_COMPLETED = 3;
    public static int ACTION_NOT_COMPLETED_FORGOT = 4;
    public static int ACTION_NOT_COMPLETED_NO_OPPORTUNITY = 5;

    private static class PromiseActionColumns {
        static String TIMESTAMP = "timestamp";
        static String PROMISE_ID = "promiseid";
        static String ACTION = "action";
    }

    public static class PromiseTableColumns {
        static String PROMISE_ID = "promiseid";
        static String PROMISE_CONTENTS = "promise";
    }

    private static final String PROMISE_ACTIONS_TABLE_CREATE = "CREATE TABLE " +
            PROMISE_ACTIONS_TABLE_NAME + " (" + PromiseActionColumns.TIMESTAMP + " INTEGER, " +
            PromiseActionColumns.PROMISE_ID + " STRING, " + PromiseActionColumns.ACTION +
            " INTEGER);";

    private static final String PROMISE_TABLE_CREATE = "CREATE TABLE " + PROMISE_TABLE_NAME + " (" +
            PromiseTableColumns.PROMISE_ID + " STRING, " + PromiseTableColumns.PROMISE_CONTENTS +
            " STRING);";

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
        super(context, "PromiseDatabase", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PROMISE_ACTIONS_TABLE_CREATE);
        sqLiteDatabase.execSQL(PROMISE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void logAction(long timestamp, Promise promise, int action) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PromiseActionColumns.TIMESTAMP, timestamp);
        values.put(PromiseActionColumns.PROMISE_ID, promise.getPromiseId());
        values.put(PromiseActionColumns.ACTION, action);
        db.insert(PROMISE_ACTIONS_TABLE_NAME, null, values);

        // TODO: Should this be in a separate call?
        ContentValues moreValues = new ContentValues();
        moreValues.put(PromiseTableColumns.PROMISE_CONTENTS, promise.getPromiseText());
        moreValues.put(PromiseTableColumns.PROMISE_ID, promise.getPromiseId());
        db.insertWithOnConflict(PROMISE_TABLE_NAME, null, moreValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean containsPromise(String promiseId) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + PROMISE_TABLE_NAME +
                " WHERE " + PromiseTableColumns.PROMISE_ID + " = " + promiseId, null);
        boolean result = c.getCount() > 0;
        c.close();
        return result;
    }

    public Set<String> getAllPromiseIds() {
        Cursor c = getReadableDatabase().query(PROMISE_ACTIONS_TABLE_NAME,
                new String[] {PromiseActionColumns.PROMISE_ID}, null, null, null, null, null);
        Set<String> result = new HashSet<>();
        c.moveToFirst();
        while (c.moveToNext()) {
            result.add(c.getString(0));
        }
        c.close();
        return result;
    }
}
