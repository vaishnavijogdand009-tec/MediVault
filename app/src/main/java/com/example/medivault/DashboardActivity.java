package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvUserName;
    private TextView tvBloodGroup;
    private TextView tvAllergies;

    private MaterialCardView cardEmergencyProfile;
    private MaterialCardView cardMedicalRecords;
    private MaterialCardView cardEmergencyContacts;
    private MaterialCardView cardDocuments;
    private MaterialCardView cardEmergencyQR;
    private MaterialCardView cardSettings;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        initializeViews();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser =
                firebaseAuth.getCurrentUser();

        if (currentUser == null) {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    LoginActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();

            return;
        }

        currentUserId = currentUser.getUid();

        setupClickListeners();

        loadUserName();
        loadEmergencyInformation();
    }

    private void initializeViews() {

        tvUserName = findViewById(R.id.tvUserName);

        // Dashboard emergency information
        tvBloodGroup = findViewById(R.id.tvBloodGroup);
        tvAllergies = findViewById(R.id.tvAllergies);

        cardEmergencyProfile =
                findViewById(R.id.cardEmergencyProfile);

        cardMedicalRecords =
                findViewById(R.id.cardMedicalRecords);

        cardEmergencyContacts =
                findViewById(R.id.cardEmergencyContacts);

        cardDocuments =
                findViewById(R.id.cardDocuments);

        cardEmergencyQR =
                findViewById(R.id.cardEmergencyQR);

        cardSettings =
                findViewById(R.id.cardSettings);
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

    // =========================================================
    // LOAD USER NAME
    // =========================================================

    private void loadUserName() {

        firestore.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        String fullName =
                                documentSnapshot.getString("fullName");

                        if (fullName != null &&
                                !fullName.trim().isEmpty()) {

                            tvUserName.setText(fullName);
                        }
                    }
                });
    }

    // =========================================================
    // LOAD EMERGENCY INFORMATION
    // =========================================================

    private void loadEmergencyInformation() {

        firestore.collection("emergency_profiles")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        tvBloodGroup.setText("Not Added");
                        tvAllergies.setText("Not Added");

                        return;
                    }

                    String bloodGroup =
                            documentSnapshot.getString(
                                    "bloodGroup"
                            );

                    String allergies =
                            documentSnapshot.getString(
                                    "allergies"
                            );


                    // -----------------------------
                    // BLOOD GROUP
                    // -----------------------------

                    if (bloodGroup != null &&
                            !bloodGroup.trim().isEmpty()) {

                        tvBloodGroup.setText(
                                bloodGroup
                        );

                    } else {

                        tvBloodGroup.setText(
                                "Not Added"
                        );
                    }


                    // -----------------------------
                    // ALLERGIES
                    // -----------------------------

                    if (allergies != null &&
                            !allergies.trim().isEmpty()) {

                        tvAllergies.setText(
                                allergies
                        );

                    } else {

                        tvAllergies.setText(
                                "Not Added"
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            DashboardActivity.this,
                            "Unable to load emergency information",
                            Toast.LENGTH_SHORT
                    ).show();

                    tvBloodGroup.setText("Not Added");
                    tvAllergies.setText("Not Added");
                });
    }

    // =========================================================
    // REFRESH WHEN RETURNING FROM EMERGENCY PROFILE
    // =========================================================

    @Override
    protected void onResume() {
        super.onResume();

        if (firestore != null &&
                currentUserId != null) {

            loadEmergencyInformation();
            loadUserName();
        }
    }
}