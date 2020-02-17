package com.example.homework1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        String username = getIntent().getStringExtra("username");
        TextView welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        welcomeTextView.setText("Welcome, " + username + "!");

    }
}
