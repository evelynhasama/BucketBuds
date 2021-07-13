package com.example.bucketbuds;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
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
        btnSignup.setOnClickListener(new View.OnClickListener() {
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
                }
            }
        });
    }

    private void signUpUser(String username, String fname, String lname, String email, String password){
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.put("firstName", fname);
        user.put("lastName", lname);
        user.setPassword(password);
        user.setEmail(email);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(SignupActivity.this, "Failed to sign up", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Issue with sign up", e);
                    return;
                }
                goMainActivity();
                Toast.makeText(SignupActivity.this, "Successfully signed up", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void goMainActivity(){
        // Navigate to main activity through intent
        Toast.makeText(SignupActivity.this, "Going to main activity", Toast.LENGTH_LONG).show();
    }

}