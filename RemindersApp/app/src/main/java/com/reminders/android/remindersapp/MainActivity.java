package com.reminders.android.remindersapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPromise();

        final ViewGroup promiseContentPrompt = (ViewGroup) findViewById(R.id.promise_prompt);
        final ViewGroup promiseContentAccepted = (ViewGroup) findViewById(R.id.promise_accepted);

        promiseContentAccepted.setVisibility(View.GONE);
        promiseContentPrompt.setVisibility(View.VISIBLE);

        findViewById(R.id.btn_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promiseContentAccepted.setVisibility(View.VISIBLE);
                promiseContentPrompt.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.btn_new_suggestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void loadPromise() {
        String promiseText = "I promise not to eat a cute cat for dinner";
        ((TextView) findViewById(R.id.promise_content)).setText(promiseText);
        ((TextView) findViewById(R.id.promise_content_accepted)).setText(promiseText);
    }


}
