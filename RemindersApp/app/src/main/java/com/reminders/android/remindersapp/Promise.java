package com.reminders.android.remindersapp;

/**
 * Represents a promise.
 */
public class Promise {
    private String mPromiseId;
    private String mPromiseText;

    public Promise(String promiseId, String promiseText) {
        mPromiseId = promiseId;
        mPromiseText = promiseText;
    }

    public String getPromiseId() {
        return mPromiseId;
    }

    public String getPromiseText() {
        return mPromiseText;
    }
}
