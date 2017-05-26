package com.reminders.android.remindersapp;

import java.util.List;

/**
 * Represents a promise a user could make (or not).
 */
public class PromiseList {
    public List<String> promises;

    public PromiseList() {

    }

    public PromiseList(List<String> promises) {
        this.promises = promises;
    }
}
