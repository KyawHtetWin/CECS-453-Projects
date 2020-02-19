package com.example.homework1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
        }

        usernameEditText = (EditText) findViewById(R.id.login_editText_username);
        passwordEditText = (EditText) findViewById(R.id.login_editText_password);

        View.OnFocusChangeListener editTextListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                switch (v.getId()) {

                    case R.id.login_editText_username:

                        String username = usernameEditText.getText().toString();

                        if (!hasFocus) {
                            if (username.isEmpty()) {
                                usernameEditText.setError("Username cannot be empty");
                            }
                        }
                        break;

                    case R.id.login_editText_password:

                        String password = passwordEditText.getText().toString();

                        if (!hasFocus) {
                            if (password.isEmpty()) {
                                passwordEditText.setError("Password cannot be empty");
                            }
                        }
                        break;
                }
            }
        };

        usernameEditText.setOnFocusChangeListener(editTextListener);
        passwordEditText.setOnFocusChangeListener(editTextListener);

    }

    public void loginButtonPressed(View view) {

        if (usernameEditText.getError() == null && passwordEditText.getError() == null) {

            String username = usernameEditText.getText().toString().toLowerCase();
            String password = passwordEditText.getText().toString();

            boolean fieldEmpty = false;

            if (username.isEmpty()) {
                usernameEditText.setError("Username cannot be empty");
                fieldEmpty = true;
            }

            if (password.isEmpty()) {
                passwordEditText.setError("Password cannot be empty");
                fieldEmpty = true;
            }

            if (!fieldEmpty) {

                boolean validCredential = checkCredential(username, password);

                if (validCredential) {

                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);

                } else {
                    passwordEditText.setText("");
                    Toast.makeText(LoginActivity.this, "Login failed: Invalid username or password!", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Toast.makeText(this, "Please fix error(s) above", Toast.LENGTH_SHORT).show();
        }

    }

    public void signUpButtonPressed(View v) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, SignupActivity.class);
        bundle.putSerializable("credentials", credentials);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public boolean checkCredential(String username, String password) {

        if (credentials.get(username) != null) {
            return credentials.get(username).equals(password);
        } else {
            return false;
        }
    }


}
