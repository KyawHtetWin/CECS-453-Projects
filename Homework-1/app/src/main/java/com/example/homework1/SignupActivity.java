package com.example.homework1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText retypePasswordEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private Button signupButton;
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
        signupButton = (Button) findViewById(R.id.signup_button_signMeUp);

        credentials = (HashMap) getIntent().getSerializableExtra("username");

    }



    public void signMeUpButtonPressed(View v) {
        // TODO: implement function
    }

    public void verifyForm() {
        // TODO: implement function
    }










}






