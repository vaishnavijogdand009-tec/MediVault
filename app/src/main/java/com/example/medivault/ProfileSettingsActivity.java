package com.example.medivault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileSettingsActivity extends AppCompatActivity {

    private TextView tvBack;
    private TextView tvUserInitial;

    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;

    private MaterialButton btnSaveChanges;
    private MaterialButton btnLogout;

    private MaterialCardView cardSecurity;
    private MaterialCardView cardAccessHistory;

    private SharedPreferences preferences;

    private static final String PREF_NAME = "MediVaultUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        initializeViews();

        preferences = getSharedPreferences(
                PREF_NAME,
                MODE_PRIVATE
        );

        loadUserDetails();
        setupClickListeners();
    }

    private void initializeViews() {

        tvBack = findViewById(R.id.tvBack);
        tvUserInitial = findViewById(R.id.tvUserInitial);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnLogout = findViewById(R.id.btnLogout);

        cardSecurity = findViewById(R.id.cardSecurity);
        cardAccessHistory = findViewById(R.id.cardAccessHistory);
    }

    private void loadUserDetails() {

        String name = preferences.getString(
                "name",
                "MediVault User"
        );

        String email = preferences.getString(
                "email",
                ""
        );

        String phone = preferences.getString(
                "phone",
                ""
        );

        etName.setText(name);
        etEmail.setText(email);
        etPhone.setText(phone);

        if (!name.isEmpty()) {
            tvUserInitial.setText(
                    String.valueOf(
                            name.charAt(0)
                    ).toUpperCase()
            );
        }
    }

    private void setupClickListeners() {

        tvBack.setOnClickListener(v -> finish());

        btnSaveChanges.setOnClickListener(
                v -> saveChanges()
        );

        cardSecurity.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Security settings will be available with Firebase Authentication",
                    Toast.LENGTH_SHORT
            ).show();
        });

        cardAccessHistory.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileSettingsActivity.this,
                    AccessHistoryActivity.class
            );

            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void saveChanges() {

        String name = etName.getText()
                .toString()
                .trim();

        String email = etEmail.getText()
                .toString()
                .trim();

        String phone = etPhone.getText()
                .toString()
                .trim();

        if (name.isEmpty()) {
            etName.setError(
                    "Please enter your name"
            );
            etName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError(
                    "Please enter your email"
            );
            etEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError(
                    "Please enter your phone number"
            );
            etPhone.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            etPhone.setError(
                    "Please enter a valid phone number"
            );
            etPhone.requestFocus();
            return;
        }

        preferences.edit()
                .putString("name", name)
                .putString("email", email)
                .putString("phone", phone)
                .apply();

        tvUserInitial.setText(
                String.valueOf(
                        name.charAt(0)
                ).toUpperCase()
        );

        Toast.makeText(
                this,
                "Profile updated successfully",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void logout() {

        /*
         * Firebase Authentication sign-out
         * will be connected here later.
         */

        Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
        ).show();

        Intent intent = new Intent(
                ProfileSettingsActivity.this,
                MainActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}