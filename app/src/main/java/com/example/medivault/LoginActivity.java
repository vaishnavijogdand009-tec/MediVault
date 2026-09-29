package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MediVaultLogin";

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;

    private MaterialButton btnLogin;

    private TextView tvCreateAccount;
    private TextView tvForgotPassword;
    private TextView tvBack;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);

        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvBack = findViewById(R.id.tvBack);
    }

    private void setupClickListeners() {

        // Back
        tvBack.setOnClickListener(v -> finish());

        // Login
        btnLogin.setOnClickListener(v -> {

            // DEBUG: button click check
            Toast.makeText(
                    LoginActivity.this,
                    "Login button clicked",
                    Toast.LENGTH_SHORT
            ).show();

            loginUser();
        });

        // Create Account
        tvCreateAccount.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });

        // Forgot Password
        tvForgotPassword.setOnClickListener(v -> resetPassword());
    }

    private void loginUser() {

        String email = etEmail.getText() != null
                ? etEmail.getText().toString().trim()
                : "";

        String password = etPassword.getText() != null
                ? etPassword.getText().toString()
                : "";

        // Email validation
        if (TextUtils.isEmpty(email)) {

            etEmail.setError("Please enter your email");
            etEmail.requestFocus();

            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();

            return;
        }

        // Password validation
        if (TextUtils.isEmpty(password)) {

            etPassword.setError("Please enter your password");
            etPassword.requestFocus();

            return;
        }

        // Disable button while logging in
        btnLogin.setEnabled(false);
        btnLogin.setText("Signing In...");

        Log.d(TAG, "Attempting Firebase login for: " + email);

        firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null) {

                            Log.d(TAG, "Login successful. UID: " + user.getUid());

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login successful",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    LoginActivity.this,
                                    DashboardActivity.class
                            );

                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);
                            finish();

                        } else {

                            btnLogin.setEnabled(true);
                            btnLogin.setText("Login");

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login succeeded but user session was not found",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } else {

                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");

                        String errorMessage;

                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();

                            Log.e(
                                    TAG,
                                    "Firebase login failed",
                                    task.getException()
                            );

                        } else {
                            errorMessage = "Login failed";
                        }

                        Toast.makeText(
                                LoginActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void resetPassword() {

        String email = etEmail.getText() != null
                ? etEmail.getText().toString().trim()
                : "";

        if (TextUtils.isEmpty(email)) {

            etEmail.setError("Enter your email first");
            etEmail.requestFocus();

            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();

            return;
        }

        firebaseAuth
                .sendPasswordResetEmail(email)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                LoginActivity.this,
                                "Password reset email sent",
                                Toast.LENGTH_LONG
                        ).show();

                    } else {

                        String message = task.getException() != null
                                ? task.getException().getMessage()
                                : "Unable to send reset email";

                        Toast.makeText(
                                LoginActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}