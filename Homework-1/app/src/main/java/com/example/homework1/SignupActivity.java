package com.example.homework1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText retypePasswordEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private HashMap<String,  String> credentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = (EditText) findViewById(R.id.signup_editText_username);
        passwordEditText = (EditText) findViewById(R.id.signup_editText_password);
        retypePasswordEditText = (EditText) findViewById(R.id.signup_editText_retypePassword);
        emailEditText = (EditText) findViewById(R.id.signup_editText_email);
        phoneEditText = (EditText) findViewById(R.id.signup_editText_phone);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
            credentials = (HashMap<String, String>) bundle.getSerializable("credentials");
        else {
            credentials = new HashMap<>();
            Toast.makeText(this, "Bundle is null", Toast.LENGTH_SHORT).show();
        }

        View.OnFocusChangeListener editTextListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                switch (v.getId()) {

                    case R.id.signup_editText_username:
                        String username = usernameEditText.getText().toString().toLowerCase();

                        if (!hasFocus) {
                            if (username.isEmpty()) {
                                usernameEditText.setError("Username cannot be empty");
                            } else if (credentials.containsKey(username)) {
                                usernameEditText.setError("Username already taken");
                            }
                        }
                        break;

                    case R.id.signup_editText_password:
                        String password = passwordEditText.getText().toString();
                        String retypePassword = retypePasswordEditText.getText().toString();

                        if (!hasFocus) {
                            if (password.isEmpty()) {
                                passwordEditText.setError("Password cannot be empty");
                            } else if (!retypePassword.isEmpty() && !retypePassword.equals(password)) {
                                    retypePasswordEditText.setError("Passwords do not match");
                            } else {
                                retypePasswordEditText.setError(null);
                            }
                        }
                        break;

                    case R.id.signup_editText_retypePassword:

                        password = passwordEditText.getText().toString();
                        retypePassword = retypePasswordEditText.getText().toString();

                        if (!hasFocus) {
                            if (retypePassword.isEmpty()) {
                                retypePasswordEditText.setError("Password cannot be empty");
                            } else if (!retypePassword.equals(password)) {
                                retypePasswordEditText.setError("Passwords do not match");
                            }
                        }
                        break;

                    case R.id.signup_editText_email:

                        String email = emailEditText.getText().toString();

                        if (!hasFocus) {
                            if (emailEditText.getText().toString().isEmpty()) {
                                emailEditText.setError("Email cannot be empty");
                            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                emailEditText.setError("Email not valid");
                            }
                        }
                        break;

                    case R.id.signup_editText_phone:
                        String phoneNumber = phoneEditText.getText().toString();

                        if (!hasFocus) {
                            if (phoneEditText.getText().toString().isEmpty()) {
                                phoneEditText.setError("Phone cannot be empty");
                            } else if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
                                phoneEditText.setError("Phone not valid");
                            }
                        }

                }
            }
        };

        usernameEditText.setOnFocusChangeListener(editTextListener);
        passwordEditText.setOnFocusChangeListener(editTextListener);
        retypePasswordEditText.setOnFocusChangeListener(editTextListener);
        emailEditText.setOnFocusChangeListener(editTextListener);
        phoneEditText.setOnFocusChangeListener(editTextListener);

    }

    public void signMeUpButtonPressed(View v) {

        if (formIsValid()) {

            String username = usernameEditText.getText().toString().toLowerCase();
            String password = passwordEditText.getText().toString();

            credentials.put(username, password);
            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();

            Bundle bundle = new Bundle();
            Intent intent = new Intent(this, LoginActivity.class);
            bundle.putSerializable("credentials", credentials);
            intent.putExtras(bundle);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Please fix error(s) above", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean formIsValid() {

        boolean hasNoError = true;
        boolean formNotEmpty = true;

        usernameEditText.clearFocus();
        passwordEditText.clearFocus();
        retypePasswordEditText.clearFocus();
        emailEditText.clearFocus();
        phoneEditText.clearFocus();

        if (usernameEditText.getError() != null ||  passwordEditText.getError() != null ||
                retypePasswordEditText.getError() != null || emailEditText.getError() != null ||
                phoneEditText.getError() != null) {

            hasNoError = false;
        }

        String username = usernameEditText.getText().toString().toLowerCase();
        String password = passwordEditText.getText().toString();
        String retypePassword = retypePasswordEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        if (username.isEmpty()) {
            usernameEditText.setError("Username cannot be empty");
            formNotEmpty = false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            formNotEmpty = false;
        }

        if (retypePassword.isEmpty()) {
            retypePasswordEditText.setError("Password cannot be empty");
            formNotEmpty = false;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Email cannot be empty");
            formNotEmpty = false;
        }

        if (phone.isEmpty()) {
            phoneEditText.setError("Phone cannot be empty");
            formNotEmpty = false;
        }

        return hasNoError && formNotEmpty;
    }

}