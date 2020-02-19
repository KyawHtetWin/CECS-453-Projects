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
        username = titleCase(username);
        TextView welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        welcomeTextView.setText("Welcome, " + username + "!");

    }

    // This function simply returns a nicely titlecase formatted string
    private static String titleCase(String string) {
        String[] parts = string.split(" ");
        StringBuilder stringBuilder = new StringBuilder(string.length());
        for(String part: parts) {
            char[] charArray = part.toLowerCase().toCharArray();
            charArray[0] = Character.toUpperCase(charArray[0]);
            stringBuilder.append(new String(charArray)).append(" ");
        }
        return stringBuilder.toString().trim();
    }
}
