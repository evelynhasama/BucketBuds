package com.example.bucketbuds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etEmail;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etUsernameSA);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmailSA);
        etPassword = findViewById(R.id.etPasswordSA);
        etConfirmPassword = findViewById(R.id.etConfirmPasswordSA);

        btnSignup = findViewById(R.id.btnSignup);

        View.OnClickListener signupClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String fname = etFirstName.getText().toString();
                String lname = etLastName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confpassword = etConfirmPassword.getText().toString();
                if (password.equals(confpassword)) {
                    signUpUser(username, fname, lname, email, password);
                } else {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    etConfirmPassword.setText("", TextView.BufferType.EDITABLE);
                    etPassword.setText("", TextView.BufferType.EDITABLE);
                }
            }
        };
        btnSignup.setOnClickListener(signupClickListener);
    }

    private void signUpUser(String username, String fname, String lname, String email, String password){
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.put(User.KEY_FIRST_NAME, fname);
        user.put(User.KEY_LAST_NAME, lname);
        user.setPassword(password);
        user.setEmail(email);
        SignUpCallback signUpCallback = new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    signUpFailedToast();
                    Log.e(TAG, "Issue with user sign up", e);
                    return;
                }
                UserPub userPub = new UserPub();
                userPub.setUser(user);
                userPub.saveInBackground(userPubSaveCallback(user, userPub));
            }
        };
        user.signUpInBackground(signUpCallback);
    }

    public void goMainActivity(){
        // Navigate to main activity through intent
        Intent intent  = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void signUpFailedToast(){
        Toast.makeText(SignupActivity.this, "Failed to sign up", Toast.LENGTH_LONG).show();
    }

    // helper method for the innermost save callback, saving userPub to user
    public SaveCallback finalUserSaveCallback() {
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    signUpFailedToast();
                    Log.e(TAG, "Issue with saving public user to profile", e);
                    return;
                }
                goMainActivity();
                Toast.makeText(SignupActivity.this, "Successfully signed up", Toast.LENGTH_SHORT).show();
            }
        };
    }

    // helper method for save callback, saving creating userPub pointing to user
    public SaveCallback userPubSaveCallback(ParseUser user, UserPub userPub) {
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    signUpFailedToast();
                    Log.e(TAG, "Issue with public user creation", e);
                    return;
                }
                user.put(User.KEY_USER_PUB, userPub);
                user.saveInBackground(finalUserSaveCallback());
            }
        };
    }

}