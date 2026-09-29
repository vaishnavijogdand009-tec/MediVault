package com.example.medivault;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

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

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private static final String PREF_NAME = "MediVaultProfile";

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_emergency_profile);

        initializeViews();

        preferences = getSharedPreferences(
                PREF_NAME,
                MODE_PRIVATE
        );

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        currentUserId = currentUser.getUid();

        loadSavedProfile();

        loadProfileFromFirestore();

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void initializeViews() {

        etBloodGroup = findViewById(R.id.etBloodGroup);

        etAllergies = findViewById(R.id.etAllergies);

        etEmergencyContactName =
                findViewById(R.id.etEmergencyContactName);

        etEmergencyContactPhone =
                findViewById(R.id.etEmergencyContactPhone);

        cbBloodGroup =
                findViewById(R.id.cbBloodGroup);

        cbAllergies =
                findViewById(R.id.cbAllergies);

        cbEmergencyContact =
                findViewById(R.id.cbEmergencyContact);

        btnSaveProfile =
                findViewById(R.id.btnSaveProfile);
    }

    private void saveProfile() {

        String bloodGroup =
                etBloodGroup.getText()
                        .toString()
                        .trim();

        String allergies =
                etAllergies.getText()
                        .toString()
                        .trim();

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

        boolean showBloodGroup =
                cbBloodGroup.isChecked();

        boolean showAllergies =
                cbAllergies.isChecked();

        boolean showEmergencyContact =
                cbEmergencyContact.isChecked();


        // -----------------------------
        // VALIDATION
        // -----------------------------

        if (TextUtils.isEmpty(bloodGroup)) {

            etBloodGroup.setError(
                    "Please enter your blood group"
            );

            etBloodGroup.requestFocus();
            return;
        }

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


        // -----------------------------
        // DISABLE BUTTON
        // -----------------------------

        btnSaveProfile.setEnabled(false);
        btnSaveProfile.setText("Saving...");


        // -----------------------------
        // MEDIVault EMERGENCY ID
        // -----------------------------

        /*
         * Same ID will later be used by QR.
         *
         * Example:
         * MV-748ayresaBoPO...
         */

        String emergencyId =
                "MV-" + currentUserId;


        // -----------------------------
        // PUBLIC EMERGENCY FIELDS
        // -----------------------------

        Map<String, Object> publicFields =
                new HashMap<>();

        publicFields.put(
                "bloodGroup",
                showBloodGroup
        );

        publicFields.put(
                "allergies",
                showAllergies
        );

        publicFields.put(
                "emergencyContact",
                showEmergencyContact
        );


        // -----------------------------
        // EMERGENCY CONTACT
        // -----------------------------

        Map<String, Object> contactData =
                new HashMap<>();

        contactData.put(
                "userId",
                currentUserId
        );

        contactData.put(
                "name",
                contactName
        );

        contactData.put(
                "phone",
                contactPhone
        );

        contactData.put(
                "relationship",
                "Emergency Contact"
        );

        contactData.put(
                "updatedAt",
                FieldValue.serverTimestamp()
        );


        /*
         * Use user UID as the contact document ID.
         *
         * This prevents creating a new contact
         * every time the user saves the profile.
         */

        firestore.collection("emergency_contacts")
                .document(currentUserId)
                .set(contactData)
                .addOnSuccessListener(unused -> {

                    saveEmergencyProfile(
                            emergencyId,
                            bloodGroup,
                            allergies,
                            publicFields
                    );

                })
                .addOnFailureListener(e -> {

                    btnSaveProfile.setEnabled(true);
                    btnSaveProfile.setText("Save Profile");

                    Toast.makeText(
                            EmergencyProfileActivity.this,
                            "Emergency contact could not be saved: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    private void saveEmergencyProfile(
            String emergencyId,
            String bloodGroup,
            String allergies,
            Map<String, Object> publicFields
    ) {

        // -----------------------------
        // EMERGENCY PROFILE
        // -----------------------------

        Map<String, Object> profileData =
                new HashMap<>();

        profileData.put(
                "profileId",
                emergencyId
        );

        profileData.put(
                "userId",
                currentUserId
        );

        profileData.put(
                "bloodGroup",
                bloodGroup
        );

        profileData.put(
                "allergies",
                allergies
        );

        profileData.put(
                "emergencyContactId",
                currentUserId
        );

        profileData.put(
                "publicEmergencyFields",
                publicFields
        );

        profileData.put(
                "updatedAt",
                FieldValue.serverTimestamp()
        );


        /*
         * Document ID = Firebase UID
         *
         * So every user has one emergency profile.
         */

        firestore.collection("emergency_profiles")
                .document(currentUserId)
                .set(profileData)
                .addOnSuccessListener(unused -> {

                    // -----------------------------
                    // LOCAL CACHE
                    // -----------------------------

                    preferences.edit()
                            .putString(
                                    "profileId",
                                    emergencyId
                            )
                            .putString(
                                    "bloodGroup",
                                    bloodGroup
                            )
                            .putString(
                                    "allergies",
                                    allergies
                            )
                            .putString(
                                    "contactName",
                                    etEmergencyContactName
                                            .getText()
                                            .toString()
                                            .trim()
                            )
                            .putString(
                                    "contactPhone",
                                    etEmergencyContactPhone
                                            .getText()
                                            .toString()
                                            .trim()
                            )
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


                    btnSaveProfile.setEnabled(true);
                    btnSaveProfile.setText("Save Profile");


                    Toast.makeText(
                            EmergencyProfileActivity.this,
                            "Emergency profile saved successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                })
                .addOnFailureListener(e -> {

                    btnSaveProfile.setEnabled(true);
                    btnSaveProfile.setText("Save Profile");

                    Toast.makeText(
                            EmergencyProfileActivity.this,
                            "Profile could not be saved: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    // =========================================================
    // LOAD PROFILE FROM FIRESTORE
    // =========================================================

    private void loadProfileFromFirestore() {

        firestore.collection("emergency_profiles")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {
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

                    if (bloodGroup != null) {

                        etBloodGroup.setText(
                                bloodGroup
                        );
                    }

                    if (allergies != null) {

                        etAllergies.setText(
                                allergies
                        );
                    }


                    // -----------------------------
                    // PUBLIC FIELDS
                    // -----------------------------

                    Map<String, Object> publicFields =
                            (Map<String, Object>)
                                    documentSnapshot.get(
                                            "publicEmergencyFields"
                                    );

                    if (publicFields != null) {

                        Object bloodGroupValue =
                                publicFields.get(
                                        "bloodGroup"
                                );

                        Object allergiesValue =
                                publicFields.get(
                                        "allergies"
                                );

                        Object contactValue =
                                publicFields.get(
                                        "emergencyContact"
                                );

                        if (bloodGroupValue instanceof Boolean) {

                            cbBloodGroup.setChecked(
                                    (Boolean) bloodGroupValue
                            );
                        }

                        if (allergiesValue instanceof Boolean) {

                            cbAllergies.setChecked(
                                    (Boolean) allergiesValue
                            );
                        }

                        if (contactValue instanceof Boolean) {

                            cbEmergencyContact.setChecked(
                                    (Boolean) contactValue
                            );
                        }
                    }


                    // -----------------------------
                    // LOAD CONTACT
                    // -----------------------------

                    loadEmergencyContactFromFirestore();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            EmergencyProfileActivity.this,
                            "Unable to load profile: "
                                    + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    private void loadEmergencyContactFromFirestore() {

        firestore.collection("emergency_contacts")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {
                        return;
                    }

                    String name =
                            documentSnapshot.getString("name");

                    String phone =
                            documentSnapshot.getString("phone");

                    if (name != null) {

                        etEmergencyContactName.setText(
                                name
                        );
                    }

                    if (phone != null) {

                        etEmergencyContactPhone.setText(
                                phone
                        );
                    }
                });
    }


    // =========================================================
    // LOCAL CACHE FALLBACK
    // =========================================================

    private void loadSavedProfile() {

        String bloodGroup =
                preferences.getString(
                        "bloodGroup",
                        ""
                );

        String allergies =
                preferences.getString(
                        "allergies",
                        ""
                );

        String contactName =
                preferences.getString(
                        "contactName",
                        ""
                );

        String contactPhone =
                preferences.getString(
                        "contactPhone",
                        ""
                );

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


        etBloodGroup.setText(
                bloodGroup
        );

        etAllergies.setText(
                allergies
        );

        etEmergencyContactName.setText(
                contactName
        );

        etEmergencyContactPhone.setText(
                contactPhone
        );

        cbBloodGroup.setChecked(
                showBloodGroup
        );

        cbAllergies.setChecked(
                showAllergies
        );

        cbEmergencyContact.setChecked(
                showEmergencyContact
        );
    }
}