package com.example.homework1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private HashMap<String, String> credentials;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createComponenets();
        credentials.put("ben", "password");

    }


    public void createComponenets() {
        usernameEditText = (EditText) findViewById(R.id.login_editText_username);
        passwordEditText = (EditText) findViewById(R.id.login_editText_password);
        credentials = new HashMap();
    }


    // TODO : fix NPE when login button pressed with empty username/password fields
    public void loginButtonPressed(View view) {

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();


        Log.i("Username", username);
        Log.i("Password", password);


        if (!username.isEmpty() && !password.isEmpty()) {

            if (credentials.get(username).equals(password)) {

                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);

            } else {
                Toast.makeText(LoginActivity.this, "Login failed: Invalid username or password", Toast.LENGTH_SHORT).show();
            }

        }

        if (username.isEmpty()) {
            usernameEditText.setError("Username cannot be empty");

        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
        }

    }

    public void signupButtonPressed(View v) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        intent.putExtra("credentials", credentials);
        startActivity(intent);
    }


}
