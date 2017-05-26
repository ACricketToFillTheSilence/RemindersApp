package com.reminders.android.remindersapp;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_PROMISE = "key_promise";
    private static final String KEY_PROMISE_ACCEPTED = "key_promise_accepted";

    private String mPromise;
    private boolean mPromiseAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewGroup promiseContentPrompt = (ViewGroup) findViewById(R.id.promise_prompt);
        final ViewGroup promiseContentAccepted = (ViewGroup) findViewById(R.id.promise_accepted);

        if (savedInstanceState != null) {
            mPromise = savedInstanceState.getString(KEY_PROMISE);
            ((TextView) findViewById(R.id.promise_content)).setText(mPromise);
            ((TextView) findViewById(R.id.promise_content_accepted)).setText(mPromise);
            mPromiseAccepted = savedInstanceState.getBoolean(KEY_PROMISE_ACCEPTED);
            if (mPromiseAccepted) {
                promiseContentAccepted.setVisibility(View.VISIBLE);
                promiseContentPrompt.setVisibility(View.GONE);
            }
        } else {
            promiseContentAccepted.setVisibility(View.GONE);
            promiseContentPrompt.setVisibility(View.VISIBLE);
            loadNextPromise();
        }
        findViewById(R.id.btn_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promiseContentAccepted.setVisibility(View.VISIBLE);
                promiseContentPrompt.setVisibility(View.GONE);
                logPromiseAcceptance();
            }
        });

        findViewById(R.id.btn_new_suggestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNextPromise();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (!TextUtils.isEmpty(mPromise)) {
            outState.putString(KEY_PROMISE, mPromise);
        }
        outState.putBoolean(KEY_PROMISE_ACCEPTED, mPromiseAccepted);
        super.onSaveInstanceState(outState);
    }

    private void logPromiseAcceptance() {
        mPromiseAccepted = true;
        DatabaseHelper.getInstance(getApplicationContext()).logAction(
                SystemClock.elapsedRealtime(), mPromise, DatabaseHelper.ACTION_ACCEPTED);
        // TODO: Set up the reminder and whatever.
    }

    private void loadNextPromise() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("PromiseList");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot promiseSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the promise -- pick the next unused one!
                    // Really shouldn't do this in a for loop...
                    if (DatabaseHelper.getInstance(getApplicationContext()).containsPromise(
                            promiseSnapshot.getKey())) {
                        continue;
                    }
                    mPromise = promiseSnapshot.getValue(String.class);
                    ((TextView) findViewById(R.id.promise_content)).setText(mPromise);
                    ((TextView) findViewById(R.id.promise_content_accepted)).setText(mPromise);
                    Log.d(TAG, "Value is: " + mPromise);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


}
