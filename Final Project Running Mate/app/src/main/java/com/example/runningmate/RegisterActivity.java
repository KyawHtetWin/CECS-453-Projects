/*****
 * Register Activity allows the user to register to our application using their email.
 * If the user provides all the requested fields, they will be added to the Firebase.
 */


package com.example.runningmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextPassword;
    ImageView iv_signup;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        iv_signup = findViewById(R.id.iv_signup);

        // Get the FirebaseAuth instance
        fAuth = FirebaseAuth.getInstance();

        // The user wants to sign up
        iv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = editTextName.getText().toString().trim();
                final String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if(TextUtils.isEmpty(name)) {
                    editTextEmail.setError("Full Name Required");
                    return;
                }


                if(TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Email Required");
                    return;
                }

                if(TextUtils.isEmpty(password))  {
                    editTextPassword.setError("Password Required");
                    return;
                }

                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password) &&
                        (TextUtils.isEmpty(name))) {
                    editTextEmail.setError("Email Required");
                    editTextPassword.setError("Password Required");
                    editTextEmail.setError("Full Name Required");
                    return;
                }

                // All the required fields are inputted. So create a user with email and password
                // on the Firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()) {
                                    // User added to the Firebase successful
                                    // set user display name
                                    UserProfileChangeRequest update = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                                    FirebaseUser user = fAuth.getCurrentUser();
                                    if (user != null){
                                        user.updateProfile(update);
                                    }

                                    // Take the user to the main activity
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                                }

                                else{
                                    // User not added to the Firebase
                                    Toast.makeText(getApplicationContext(), "Error" +
                                            task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                );

            }
        });


    }

    // Take the user to the login page
    public void onLoginClick(View v) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
