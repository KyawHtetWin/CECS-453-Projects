package com.example.homework1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextWatcher;

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

        // Add a TextWatcher
        retypePasswordEditText = (EditText) findViewById(R.id.signup_editText_retypePassword);
        retypePasswordEditText.addTextChangedListener(watch);

        emailEditText = (EditText) findViewById(R.id.signup_editText_email);
        phoneEditText = (EditText) findViewById(R.id.signup_editText_phone);

        //credentials = (HashMap<String, String>) getIntent().getSerializableExtra("credentials");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            credentials = (HashMap<String, String>) bundle.getSerializable("credentials");

        else {
            credentials = new HashMap<>();
            Toast.makeText(this, "Bundle is null", Toast.LENGTH_SHORT).show();

        }
    }

    TextWatcher watch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(!passwordEditText.getText().toString().equals(retypePasswordEditText.getText().toString()))
                retypePasswordEditText.setError("Password must match");
        }
    };

    public void signMeUpButtonPressed(View v) {
        String username = usernameEditText.getText().toString().toLowerCase();
        String password = passwordEditText.getText().toString();
        String retypePassword = retypePasswordEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        boolean formNotEmpty = true;

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

        if(!password.equals(retypePassword)) {
            Toast.makeText(this, "Sign up failed for unmatched password ", Toast.LENGTH_SHORT).show();
        }

        else if (formNotEmpty) {

            if (credentials.get(username) == null) {
                credentials.put(username, password);
                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                Intent intent = new Intent(this, LoginActivity.class);
                bundle.putSerializable("credentials", credentials);
                intent.putExtras(bundle);
                startActivity(intent);

            } else {
                Toast.makeText(this, "Username already exist", Toast.LENGTH_SHORT).show();
            }

        }

    }





    public void verifyForm() {
        // TODO: implement function
    }










}






