package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvCreateAccount, tvForgotPassword;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Login button
        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Please enter your email");
                etEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Please enter your password");
                etPassword.requestFocus();
                return;
            }

            /*
             * Firebase Login will be added later.
             *
             * After successful login:
             *
             * Intent intent = new Intent(
             *      LoginActivity.this,
             *      DashboardActivity.class
             * );
             * startActivity(intent);
             * finish();
             */

        });

        // Create Account
        tvCreateAccount.setOnClickListener(v -> {

            Intent intent =
                    new Intent(LoginActivity.this, RegisterActivity.class);

            startActivity(intent);

        });

        // Forgot Password
        tvForgotPassword.setOnClickListener(v -> {

            // Firebase password reset functionality
            // will be connected later.

        });

    }
}