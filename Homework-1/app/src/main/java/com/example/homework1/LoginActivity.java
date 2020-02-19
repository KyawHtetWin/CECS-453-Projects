// CECS 453 Mobile Development
// Homework 1
// Due date: Feb 20, 2020

// Team members:
// Ben Do
// Kyaw Htet Win

package com.example.homework1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    // This hashmap will stores the password for the corresponding username, which we can use
    // to verify the user login
    private HashMap<String, String> credentials;
    private EditText usernameEditText;
    private EditText passwordEditText;


    // Do the set up of the login activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle bundle = getIntent().getExtras();
        // Checks if there is any user's credentials is passed that can be used to login
        if (bundle != null)
            credentials = (HashMap<String, String>) bundle.getSerializable("credentials");
        else {
            credentials = new HashMap<>();
        }

        usernameEditText = (EditText) findViewById(R.id.login_editText_username);
        passwordEditText = (EditText) findViewById(R.id.login_editText_password);

        // Sets up the listener on the username and password edit texts to check that
        // username and password are filled in before the user presses login
        View.OnFocusChangeListener editTextListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                switch (v.getId()) {

                    case R.id.login_editText_username:

                        String username = usernameEditText.getText().toString();
                        // Notify users if the username is empty
                        if (!hasFocus) {
                            if (username.isEmpty()) {
                                usernameEditText.setError("Username cannot be empty");
                            }
                        }
                        break;

                    case R.id.login_editText_password:

                        String password = passwordEditText.getText().toString();
                        // Notify users if the password is empty
                        if (!hasFocus) {
                            if (password.isEmpty()) {
                                passwordEditText.setError("Password cannot be empty");
                            }
                        }
                        break;
                }
            }
        };

        // Register the usernameEditText and passwordEditText as listener
        usernameEditText.setOnFocusChangeListener(editTextListener);
        passwordEditText.setOnFocusChangeListener(editTextListener);

    }

    // This function gets called the when login button is pressed
    public void loginButtonPressed(View view) {


        if (usernameEditText.getError() == null && passwordEditText.getError() == null) {

            String username = usernameEditText.getText().toString().toLowerCase();
            String password = passwordEditText.getText().toString();

            boolean fieldEmpty = false;
            // Validate that the username and password fields are not empty
            if (username.isEmpty()) {
                usernameEditText.setError("Username cannot be empty");
                fieldEmpty = true;
            }

            if (password.isEmpty()) {
                passwordEditText.setError("Password cannot be empty");
                fieldEmpty = true;
            }

            // The user has entered username and login at this point
            if (!fieldEmpty) {

                boolean validCredential = checkCredential(username, password);

                // If the user exists in our credentials hashmap and is validated, login is successful,
                // the user is taken to the welcome page that gives a welcome message with their name
                if (validCredential) {

                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);

                }
                // Inform the user that login failed
                else {
                    passwordEditText.setText("");
                    Toast.makeText(LoginActivity.this, "Login failed: Invalid username or password!", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Toast.makeText(this, "Please fix error(s) above", Toast.LENGTH_SHORT).show();
        }

    }

    // This function gets called whenever the SignUp Button is pressed. Takes the user to the
    // SignupActivity, while passing our credentials hashmap to check it's the new user
    public void signUpButtonPressed(View v) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, SignupActivity.class);
        bundle.putSerializable("credentials", credentials);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    // This function simply checks the credentials of the user by checking the password of the username
    public boolean checkCredential(String username, String password) {

        if (credentials.get(username) != null) {
            return credentials.get(username).equals(password);
        } else {
            return false;
        }
    }


}
