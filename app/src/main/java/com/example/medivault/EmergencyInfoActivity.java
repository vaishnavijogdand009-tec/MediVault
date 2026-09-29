package com.example.medivault;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class EmergencyInfoActivity extends AppCompatActivity {

    private TextView tvBack;

    private View cardBloodGroup;
    private View cardAllergies;
    private View cardEmergencyContact;

    private TextView tvBloodGroup;
    private TextView tvAllergies;
    private TextView tvEmergencyContactName;
    private TextView tvEmergencyContactPhone;

    private FirebaseFirestore firestore;

    private String emergencyId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_emergency_info);

        initializeViews();

        firestore = FirebaseFirestore.getInstance();

        emergencyId = getIntent().getStringExtra("emergencyId");

        tvBack.setOnClickListener(v -> finish());

        loadEmergencyInformation();
    }

    private void initializeViews() {

        tvBack = findViewById(R.id.tvBack);

        cardBloodGroup = findViewById(R.id.cardBloodGroup);
        cardAllergies = findViewById(R.id.cardAllergies);
        cardEmergencyContact =
                findViewById(R.id.cardEmergencyContact);

        tvBloodGroup =
                findViewById(R.id.tvBloodGroup);

        tvAllergies =
                findViewById(R.id.tvAllergies);

        tvEmergencyContactName =
                findViewById(R.id.tvEmergencyContactName);

        tvEmergencyContactPhone =
                findViewById(R.id.tvEmergencyContactPhone);

        // Initially hide all information cards
        cardBloodGroup.setVisibility(View.GONE);
        cardAllergies.setVisibility(View.GONE);
        cardEmergencyContact.setVisibility(View.GONE);
    }

    private void loadEmergencyInformation() {

        if (emergencyId == null ||
                emergencyId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Emergency ID not found",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        /*
         * QR contains:
         *
         * MV-FIREBASE_USER_UID
         *
         * Our Firestore document ID is:
         *
         * FIREBASE_USER_UID
         */

        if (!emergencyId.startsWith("MV-")) {

            Toast.makeText(
                    this,
                    "Invalid MediVault Emergency ID",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        userId = emergencyId.substring(3);

        if (userId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Invalid Emergency ID",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        loadProfileFromFirestore();
    }

    private void loadProfileFromFirestore() {

        firestore.collection("emergency_profiles")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                EmergencyInfoActivity.this,
                                "Emergency profile not found",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    displayProfile(documentSnapshot);

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            EmergencyInfoActivity.this,
                            "Unable to load emergency information: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void displayProfile(
            DocumentSnapshot profile
    ) {

        String bloodGroup =
                profile.getString("bloodGroup");

        String allergies =
                profile.getString("allergies");

        String emergencyContactId =
                profile.getString("emergencyContactId");

        /*
         * publicEmergencyFields is stored as:
         *
         * publicEmergencyFields
         *     ├── bloodGroup
         *     ├── allergies
         *     └── emergencyContact
         */

        Map<String, Object> publicFields =
                (Map<String, Object>)
                        profile.get("publicEmergencyFields");

        boolean showBloodGroup = false;
        boolean showAllergies = false;
        boolean showEmergencyContact = false;

        if (publicFields != null) {

            Object bloodGroupValue =
                    publicFields.get("bloodGroup");

            Object allergiesValue =
                    publicFields.get("allergies");

            Object contactValue =
                    publicFields.get("emergencyContact");

            if (bloodGroupValue instanceof Boolean) {

                showBloodGroup =
                        (Boolean) bloodGroupValue;
            }

            if (allergiesValue instanceof Boolean) {

                showAllergies =
                        (Boolean) allergiesValue;
            }

            if (contactValue instanceof Boolean) {

                showEmergencyContact =
                        (Boolean) contactValue;
            }
        }

        // --------------------------------
        // BLOOD GROUP
        // --------------------------------

        if (showBloodGroup &&
                bloodGroup != null &&
                !bloodGroup.trim().isEmpty()) {

            tvBloodGroup.setText(bloodGroup);

            cardBloodGroup.setVisibility(
                    View.VISIBLE
            );
        }


        // --------------------------------
        // ALLERGIES
        // --------------------------------

        if (showAllergies &&
                allergies != null &&
                !allergies.trim().isEmpty()) {

            tvAllergies.setText(allergies);

            cardAllergies.setVisibility(
                    View.VISIBLE
            );
        }


        // --------------------------------
        // EMERGENCY CONTACT
        // --------------------------------

        if (showEmergencyContact &&
                emergencyContactId != null &&
                !emergencyContactId.trim().isEmpty()) {

            loadEmergencyContact(
                    emergencyContactId
            );
        }
    }

    private void loadEmergencyContact(
            String contactId
    ) {

        firestore.collection("emergency_contacts")
                .document(contactId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                EmergencyInfoActivity.this,
                                "Emergency contact not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    String name =
                            documentSnapshot.getString("name");

                    String phone =
                            documentSnapshot.getString("phone");

                    if (name == null ||
                            name.trim().isEmpty()) {

                        name = "Not available";
                    }

                    if (phone == null ||
                            phone.trim().isEmpty()) {

                        phone = "Not available";
                    }

                    tvEmergencyContactName.setText(name);

                    tvEmergencyContactPhone.setText(phone);

                    cardEmergencyContact.setVisibility(
                            View.VISIBLE
                    );
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            EmergencyInfoActivity.this,
                            "Unable to load emergency contact",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
}