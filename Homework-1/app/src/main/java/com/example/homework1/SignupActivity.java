package com.example.homework1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        credentials = (HashMap) getIntent().getSerializableExtra("credentials");
    }



    public void signMeUpButtonPressed(View v) {
        String username = usernameEditText.getText().toString();
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


        if (formNotEmpty) {

            if (credentials.get(username) == null) {
                credentials.put(username, password);
                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("credentials", credentials);
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





