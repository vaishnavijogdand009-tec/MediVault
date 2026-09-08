package com.example.medivault;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class EmergencyInfoActivity extends AppCompatActivity {

    private TextView tvBack;
    private TextView tvEmergencyId;

    private TextView tvBloodGroup;
    private TextView tvAllergies;
    private TextView tvContactName;
    private TextView tvContactPhone;

    private MaterialCardView cardBloodGroup;
    private MaterialCardView cardAllergies;
    private MaterialCardView cardEmergencyContact;

    private SharedPreferences preferences;

    private static final String PREF_NAME = "MediVaultProfile";

    private String emergencyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_info);

        initializeViews();

        preferences = getSharedPreferences(
                PREF_NAME,
                MODE_PRIVATE
        );

        emergencyId = getIntent().getStringExtra("emergencyId");

        if (emergencyId == null || emergencyId.isEmpty()) {
            emergencyId = "Unknown";
        }

        tvEmergencyId.setText(emergencyId);

        loadEmergencyInformation();

        tvBack.setOnClickListener(v -> finish());
    }

    private void initializeViews() {

        tvBack = findViewById(R.id.tvBack);
        tvEmergencyId = findViewById(R.id.tvEmergencyId);

        tvBloodGroup = findViewById(R.id.tvBloodGroup);
        tvAllergies = findViewById(R.id.tvAllergies);
        tvContactName = findViewById(R.id.tvContactName);
        tvContactPhone = findViewById(R.id.tvContactPhone);

        cardBloodGroup = findViewById(R.id.cardBloodGroup);
        cardAllergies = findViewById(R.id.cardAllergies);
        cardEmergencyContact = findViewById(R.id.cardEmergencyContact);
    }

    private void loadEmergencyInformation() {

        String bloodGroup =
                preferences.getString("bloodGroup", "Not available");

        String allergies =
                preferences.getString("allergies", "Not available");

        String contactName =
                preferences.getString("contactName", "Not available");

        String contactPhone =
                preferences.getString("contactPhone", "Not available");

        boolean showBloodGroup =
                preferences.getBoolean("showBloodGroup", true);

        boolean showAllergies =
                preferences.getBoolean("showAllergies", true);

        boolean showEmergencyContact =
                preferences.getBoolean("showEmergencyContact", true);


        // Blood Group

        if (showBloodGroup && !bloodGroup.isEmpty()) {

            tvBloodGroup.setText(bloodGroup);

            cardBloodGroup.setVisibility(
                    MaterialCardView.VISIBLE
            );

        } else {

            cardBloodGroup.setVisibility(
                    MaterialCardView.GONE
            );
        }


        // Allergies

        if (showAllergies && !allergies.isEmpty()) {

            tvAllergies.setText(allergies);

            cardAllergies.setVisibility(
                    MaterialCardView.VISIBLE
            );

        } else {

            cardAllergies.setVisibility(
                    MaterialCardView.GONE
            );
        }


        // Emergency Contact

        if (showEmergencyContact
                && !contactName.isEmpty()
                && !contactPhone.isEmpty()) {

            tvContactName.setText(contactName);
            tvContactPhone.setText(contactPhone);

            cardEmergencyContact.setVisibility(
                    MaterialCardView.VISIBLE
            );

        } else {

            cardEmergencyContact.setVisibility(
                    MaterialCardView.GONE
            );
        }


        if (!showBloodGroup
                && !showAllergies
                && !showEmergencyContact) {

            Toast.makeText(
                    this,
                    "No emergency information is available",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}