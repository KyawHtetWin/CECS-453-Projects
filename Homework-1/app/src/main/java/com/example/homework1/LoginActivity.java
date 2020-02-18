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

    public static HashMap<String, String> credentials;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
            credentials = (HashMap<String, String>) bundle.getSerializable("credentials");

        else {
            credentials = new HashMap<>();
            //Toast.makeText(LoginActivity.this, "bundle is null", Toast.LENGTH_SHORT).show();
        }

        createComponenets();
    }


    public void createComponenets() {
        usernameEditText = (EditText) findViewById(R.id.login_editText_username);
        passwordEditText = (EditText) findViewById(R.id.login_editText_password);

    }


    // TODO : fix NPE when login button pressed with empty username/password fields
    public void loginButtonPressed(View view) {

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        
        if (!username.isEmpty() && !password.isEmpty()) {

            if (credentials.get(username) != null && credentials.get(username).equals(password)) {

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
        Bundle bundle = new Bundle();

        Intent intent = new Intent(this, SignupActivity.class);
        bundle.putSerializable("credentials", credentials);
        intent.putExtras(bundle);
        //intent.putExtra("credentials", credentials);
        startActivity(intent);
    }


}
