package com.example.homework2;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.homework2.database.DBHelper;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    // declare views
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText retypePasswordEditText;
    private EditText emailEditText;
    private EditText phoneEditText;

    private DBHelper mDb;

    // set up the activity components
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // initialize views
        usernameEditText = (EditText) findViewById(R.id.signup_editText_username);
        passwordEditText = (EditText) findViewById(R.id.signup_editText_password);
        retypePasswordEditText = (EditText) findViewById(R.id.signup_editText_retypePassword);
        emailEditText = (EditText) findViewById(R.id.signup_editText_email);
        phoneEditText = (EditText) findViewById(R.id.signup_editText_phone);

        mDb = new DBHelper(getApplicationContext());

        // OnFocusChange listener for the editText views
        View.OnFocusChangeListener editTextListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                switch (v.getId()) {
                    // triggered by the username editText view
                    // shows error for empty or non alphanumeric usernames
                    case R.id.signup_editText_username:

                        ArrayList<String> mUsernameList = getUsernames();
                        String username = usernameEditText.getText().toString().toLowerCase();

                        if (!hasFocus) {

                            if (username.isEmpty()) {
                                usernameEditText.setError("Username cannot be empty");
                            } else if (!StringUtils.isAlphanumeric(username)) {
                                usernameEditText.setError("Username must be alpha-numeric");
                            } else if (mUsernameList.contains(username)) {
                                usernameEditText.setError("Username already taken");
                            }
                        }
                        break;

                    // triggered by the password editText view
                    // shows error if password is empty or both passwords do not match
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

                    // triggered by the retypePassword editText view
                    // shows error if second password empty or both passwords do match
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

                    // triggered by the email editText view
                    // shows error if email is empty or is not in correct format
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

                    // triggered by the phone editText view
                    // shows error if phone number is empty or not in correct format
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

        // add listener to views
        usernameEditText.setOnFocusChangeListener(editTextListener);
        passwordEditText.setOnFocusChangeListener(editTextListener);
        retypePasswordEditText.setOnFocusChangeListener(editTextListener);
        emailEditText.setOnFocusChangeListener(editTextListener);
        phoneEditText.setOnFocusChangeListener(editTextListener);

    }

    // onclick method for the sign me up button
    // create new user if data entered is valid, then starts login activity
    public void signMeUpButtonPressed(View v) {

        // check if user entered data is valid before creating new user
        if (formIsValid()) {

            String username = usernameEditText.getText().toString().toLowerCase();
            String password = passwordEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String phone = phoneEditText.getText().toString();

            mDb.addNewUser(username, phone, email, password);

            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();

            // start login activity and pass in the new list of credentials
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Please fix error(s) above", Toast.LENGTH_SHORT).show();
        }

    }

    // verify all entered data is valid to create new user
    public boolean formIsValid() {

        boolean hasNoError = true;
        boolean formNotEmpty = true;

        // clear focus to trigger listener to check for errors
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

        // check if editText views are empty
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

    // get list of registered usernames from sqlite database
    public ArrayList<String> getUsernames() {
        Cursor res = mDb.getUsernames();
        ArrayList<String> usernames = new ArrayList<>();

        try {
            res.moveToFirst();
            while (!res.isAfterLast()) {
                usernames.add(res.getString(0));
                res.moveToNext();
            }
        } finally {
            res.close();
        }

        return usernames;
    }

}
