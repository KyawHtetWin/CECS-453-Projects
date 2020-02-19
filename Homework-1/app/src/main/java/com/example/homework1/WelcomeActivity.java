package com.example.homework1;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    // Do the set up of Login Activity
    // Simply display a welcome message with the user's name
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        String username = getIntent().getStringExtra("username");
        username = StringUtils.capitalize(username);
        String message = "Welcome, " + username + "!";
        TextView welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        welcomeTextView.setText(message);

    }
}
