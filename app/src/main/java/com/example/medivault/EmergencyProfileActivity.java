package com.example.medivault;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

public class EmergencyProfileActivity extends AppCompatActivity {

    private TextInputEditText etBloodGroup;
    private TextInputEditText etAllergies;
    private TextInputEditText etEmergencyContactName;
    private TextInputEditText etEmergencyContactPhone;

    private MaterialCheckBox cbBloodGroup;
    private MaterialCheckBox cbAllergies;
    private MaterialCheckBox cbEmergencyContact;

    private MaterialButton btnSaveProfile;

    private SharedPreferences preferences;

    private static final String PREF_NAME = "MediVaultProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_profile);

        initializeViews();

        preferences = getSharedPreferences(
                PREF_NAME,
                MODE_PRIVATE
        );

        loadSavedProfile();

        findViewById(R.id.tvBack).setOnClickListener(v -> {
            finish();
        });

        btnSaveProfile.setOnClickListener(v -> {
            saveProfile();
        });
    }

    private void initializeViews() {

        etBloodGroup = findViewById(R.id.etBloodGroup);
        etAllergies = findViewById(R.id.etAllergies);
        etEmergencyContactName =
                findViewById(R.id.etEmergencyContactName);
        etEmergencyContactPhone =
                findViewById(R.id.etEmergencyContactPhone);

        cbBloodGroup = findViewById(R.id.cbBloodGroup);
        cbAllergies = findViewById(R.id.cbAllergies);
        cbEmergencyContact =
                findViewById(R.id.cbEmergencyContact);

        btnSaveProfile = findViewById(R.id.btnSaveProfile);
    }

    private void saveProfile() {

        String bloodGroup =
                etBloodGroup.getText().toString().trim();

        String allergies =
                etAllergies.getText().toString().trim();

        String contactName =
                etEmergencyContactName
                        .getText()
                        .toString()
                        .trim();

        String contactPhone =
                etEmergencyContactPhone
                        .getText()
                        .toString()
                        .trim();

        // Blood group validation
        if (TextUtils.isEmpty(bloodGroup)) {
            etBloodGroup.setError(
                    "Please enter your blood group"
            );
            etBloodGroup.requestFocus();
            return;
        }

        // Emergency contact validation
        if (TextUtils.isEmpty(contactName)) {
            etEmergencyContactName.setError(
                    "Please enter emergency contact name"
            );
            etEmergencyContactName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(contactPhone)) {
            etEmergencyContactPhone.setError(
                    "Please enter emergency contact phone"
            );
            etEmergencyContactPhone.requestFocus();
            return;
        }

        if (contactPhone.length() < 10) {
            etEmergencyContactPhone.setError(
                    "Please enter a valid phone number"
            );
            etEmergencyContactPhone.requestFocus();
            return;
        }

        /*
         * Local storage for the prototype.
         *
         * Firebase Firestore will later store the same
         * information in the emergency_profiles collection.
         */

        preferences.edit()
                .putString("bloodGroup", bloodGroup)
                .putString("allergies", allergies)
                .putString("contactName", contactName)
                .putString("contactPhone", contactPhone)
                .putBoolean(
                        "showBloodGroup",
                        cbBloodGroup.isChecked()
                )
                .putBoolean(
                        "showAllergies",
                        cbAllergies.isChecked()
                )
                .putBoolean(
                        "showEmergencyContact",
                        cbEmergencyContact.isChecked()
                )
                .apply();

        Toast.makeText(
                this,
                "Emergency profile saved successfully",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    private void loadSavedProfile() {

        String bloodGroup =
                preferences.getString("bloodGroup", "");

        String allergies =
                preferences.getString("allergies", "");

        String contactName =
                preferences.getString("contactName", "");

        String contactPhone =
                preferences.getString("contactPhone", "");

        boolean showBloodGroup =
                preferences.getBoolean(
                        "showBloodGroup",
                        true
                );

        boolean showAllergies =
                preferences.getBoolean(
                        "showAllergies",
                        true
                );

        boolean showEmergencyContact =
                preferences.getBoolean(
                        "showEmergencyContact",
                        true
                );

        etBloodGroup.setText(bloodGroup);
        etAllergies.setText(allergies);
        etEmergencyContactName.setText(contactName);
        etEmergencyContactPhone.setText(contactPhone);

        cbBloodGroup.setChecked(showBloodGroup);
        cbAllergies.setChecked(showAllergies);
        cbEmergencyContact.setChecked(showEmergencyContact);
    }
}