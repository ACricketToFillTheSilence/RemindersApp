package com.reminders.android.remindersapp;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_PROMISE = "key_promise";
    private static final String KEY_PROMISE_ID = "key_promise_id";
    private static final String KEY_PROMISE_ACCEPTED = "key_promise_accepted";

    private Promise mPromise;
    private boolean mPromiseAccepted = false;
    private List<Promise> mAvailablePromises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewGroup promiseContentPrompt = (ViewGroup) findViewById(R.id.promise_prompt);
        final ViewGroup promiseContentAccepted = (ViewGroup) findViewById(R.id.promise_accepted);

        if (savedInstanceState != null) {
            mPromise = new Promise(savedInstanceState.getString(KEY_PROMISE_ID),
                    savedInstanceState.getString(KEY_PROMISE));
            ((TextView) findViewById(R.id.promise_content)).setText(mPromise.getPromiseText());
            ((TextView) findViewById(R.id.promise_content_accepted)).setText(
                    mPromise.getPromiseText());
            mPromiseAccepted = savedInstanceState.getBoolean(KEY_PROMISE_ACCEPTED);
        }
        if (!mPromiseAccepted) {
            promiseContentAccepted.setVisibility(View.GONE);
            promiseContentPrompt.setVisibility(View.VISIBLE);
            loadPromisesFromFirebase();
        } else {
            promiseContentAccepted.setVisibility(View.VISIBLE);
            promiseContentPrompt.setVisibility(View.GONE);
        }

        onPromiseLoadStateChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mPromise != null) {
            outState.putString(KEY_PROMISE, mPromise.getPromiseText());
            outState.putString(KEY_PROMISE_ID, mPromise.getPromiseId());
        }
        outState.putBoolean(KEY_PROMISE_ACCEPTED, mPromiseAccepted);
        super.onSaveInstanceState(outState);
    }

    private void logPromiseAcceptance() {
        mPromiseAccepted = true;
        DatabaseHelper.getInstance(getApplicationContext()).logAction(SystemClock.elapsedRealtime(),
                mPromise, DatabaseHelper.ACTION_ACCEPTED);
        // TODO: Set up the reminder and whatever.
    }

    private void loadPromisesFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("PromiseList");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                // To avoid resetting when data is updated, we will check if the promise ID is set
                // yet.
                if (mPromise != null) {
                    return;
                }
                Set<String> usedPromiseIds =
                        DatabaseHelper.getInstance(getApplicationContext()).getAllPromiseIds();
                mAvailablePromises = new ArrayList<>();
                for (DataSnapshot promiseSnapshot: dataSnapshot.getChildren()) {
                    if (usedPromiseIds.contains(promiseSnapshot.getKey())) {
                        continue;
                    }
                    mAvailablePromises.add(new Promise(promiseSnapshot.getKey(),
                            promiseSnapshot.getValue(String.class)));
                }
                loadNextPromise();
                onPromiseLoadStateChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void loadNextPromise() {
        if (mAvailablePromises.size() > 0) {
            mPromise = mAvailablePromises.remove(0);
            ((TextView) findViewById(R.id.promise_content)).setText(
                    mPromise.getPromiseText());
            ((TextView) findViewById(R.id.promise_content_accepted)).setText(
                    mPromise.getPromiseText());
        } else {
            // TODO: Show an error about no more promises available.
        }
    }

    private void onPromiseLoadStateChanged() {
        Button acceptBtn = (Button) findViewById(R.id.btn_accept);
        Button skipButton = (Button) findViewById(R.id.btn_new_suggestion);
        if (mPromise == null) {
            acceptBtn.setEnabled(false);
            skipButton.setEnabled(false);
        } else {
            acceptBtn.setEnabled(true);
            skipButton.setEnabled(true);
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.promise_accepted).setVisibility(View.VISIBLE);
                    findViewById(R.id.promise_prompt).setVisibility(View.GONE);
                    logPromiseAcceptance();
                }
            });

            skipButton.setEnabled(mPromise != null);
            skipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Log that we skipped this one.
                    DatabaseHelper.getInstance(getApplicationContext()).logAction(
                            SystemClock.elapsedRealtime(), mPromise, DatabaseHelper.ACTION_SKIPPED);
                    loadNextPromise();
                }
            });
        }
    }
}
