package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvUserName;

    private MaterialCardView cardEmergencyProfile;
    private MaterialCardView cardMedicalRecords;
    private MaterialCardView cardEmergencyContacts;
    private MaterialCardView cardDocuments;
    private MaterialCardView cardEmergencyQR;
    private MaterialCardView cardSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {

        tvUserName = findViewById(R.id.tvUserName);

        cardEmergencyProfile = findViewById(R.id.cardEmergencyProfile);
        cardMedicalRecords = findViewById(R.id.cardMedicalRecords);
        cardEmergencyContacts = findViewById(R.id.cardEmergencyContacts);
        cardDocuments = findViewById(R.id.cardDocuments);
        cardEmergencyQR = findViewById(R.id.cardEmergencyQR);
        cardSettings = findViewById(R.id.cardSettings);
    }

    private void setupClickListeners() {

        cardEmergencyProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    EmergencyProfileActivity.class
            );

            startActivity(intent);
        });

        cardMedicalRecords.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    MedicalRecordsActivity.class
            );

            startActivity(intent);
        });

        cardEmergencyContacts.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    EmergencyContactsActivity.class
            );

            startActivity(intent);
        });

        cardDocuments.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    DocumentsActivity.class
            );

            startActivity(intent);
        });

        cardEmergencyQR.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    EmergencyQRActivity.class
            );

            startActivity(intent);
        });

        cardSettings.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    ProfileSettingsActivity.class
            );

            startActivity(intent);
        });
    }
}