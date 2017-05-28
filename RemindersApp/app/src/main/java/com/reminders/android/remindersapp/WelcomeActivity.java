package com.reminders.android.remindersapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Welcome for the first-time use of the app.
 */
public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        findViewById(R.id.btn_welcome_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueToMain();
            }
        });
    }

    private void continueToMain() {
        getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE).edit()
                .putBoolean(MainActivity.WELCOME_COMPLETED, true).apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
