package com.reminders.android.remindersapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

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
            loadNewPromise();
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
                loadNewPromise();
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
        // TODO: Set up the reminder and whatever.
    }

    private void loadNewPromise() {
        // TODO: Load a new promise that we haven't completed before.
        mPromise = new Random().nextBoolean() ? "I promise not to eat a cute cat for dinner" :
                "I promise to tell Vega she rocks";
        ((TextView) findViewById(R.id.promise_content)).setText(mPromise);
        ((TextView) findViewById(R.id.promise_content_accepted)).setText(mPromise);
    }


}
