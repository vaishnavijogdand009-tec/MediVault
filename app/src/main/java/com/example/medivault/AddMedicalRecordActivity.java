package com.example.medivault;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddMedicalRecordActivity extends AppCompatActivity {

    private AutoCompleteTextView etRecordType;
    private TextInputEditText etRecordTitle;
    private TextInputEditText etRecordDetails;
    private TextInputEditText etDocumentUrl;

    private MaterialButton btnSaveRecord;
    private TextView tvBack;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private String currentUserId;

    private final String[] recordTypes = {
            "Medical Report",
            "Prescription",
            "Diagnosis",
            "Vaccination",
            "Surgery",
            "Lab Test",
            "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_medical_record);

        initializeViews();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser =
                firebaseAuth.getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        currentUserId = currentUser.getUid();

        setupRecordTypeDropdown();
        setupClickListeners();
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        tvBack = findViewById(R.id.tvBack);

        etRecordType =
                findViewById(R.id.etRecordType);

        etRecordTitle =
                findViewById(R.id.etRecordTitle);

        etRecordDetails =
                findViewById(R.id.etRecordDetails);

        etDocumentUrl =
                findViewById(R.id.etDocumentUrl);

        btnSaveRecord =
                findViewById(R.id.btnSaveRecord);
    }

    // =========================================================
    // RECORD TYPE DROPDOWN
    // =========================================================

    private void setupRecordTypeDropdown() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        recordTypes
                );

        etRecordType.setAdapter(adapter);

        etRecordType.setOnClickListener(v ->
                etRecordType.showDropDown()
        );
    }

    // =========================================================
    // CLICK LISTENERS
    // =========================================================

    private void setupClickListeners() {

        tvBack.setOnClickListener(v ->
                finish()
        );

        btnSaveRecord.setOnClickListener(v ->
                saveMedicalRecord()
        );
    }

    // =========================================================
    // SAVE MEDICAL RECORD
    // =========================================================

    private void saveMedicalRecord() {

        String recordType =
                etRecordType
                        .getText()
                        .toString()
                        .trim();

        String title =
                etRecordTitle
                        .getText()
                        .toString()
                        .trim();

        String details =
                etRecordDetails
                        .getText()
                        .toString()
                        .trim();

        String documentUrl =
                etDocumentUrl
                        .getText()
                        .toString()
                        .trim();

        // -----------------------------------------------------
        // VALIDATION
        // -----------------------------------------------------

        if (TextUtils.isEmpty(recordType)) {

            etRecordType.setError(
                    "Please select a record type"
            );

            etRecordType.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(title)) {

            etRecordTitle.setError(
                    "Please enter record title"
            );

            etRecordTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(details)) {

            etRecordDetails.setError(
                    "Please enter medical details"
            );

            etRecordDetails.requestFocus();
            return;
        }

        // -----------------------------------------------------
        // DISABLE BUTTON
        // -----------------------------------------------------

        btnSaveRecord.setEnabled(false);
        btnSaveRecord.setText("Saving...");

        // -----------------------------------------------------
        // CREATE FIRESTORE DOCUMENT ID
        // -----------------------------------------------------

        String recordId =
                firestore
                        .collection("medical_records")
                        .document()
                        .getId();

        // -----------------------------------------------------
        // CREATE DATA
        // -----------------------------------------------------

        Map<String, Object> recordData =
                new HashMap<>();

        recordData.put(
                "recordId",
                recordId
        );

        recordData.put(
                "userId",
                currentUserId
        );

        recordData.put(
                "recordType",
                recordType
        );

        recordData.put(
                "title",
                title
        );

        recordData.put(
                "details",
                details
        );

        recordData.put(
                "documentUrl",
                documentUrl
        );

        recordData.put(
                "createdAt",
                System.currentTimeMillis()
        );

        // -----------------------------------------------------
        // SAVE TO FIRESTORE
        // -----------------------------------------------------

        firestore
                .collection("medical_records")
                .document(recordId)
                .set(recordData)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            AddMedicalRecordActivity.this,
                            "Medical record saved successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    btnSaveRecord.setEnabled(true);
                    btnSaveRecord.setText(
                            "Save Record"
                    );

                    Toast.makeText(
                            AddMedicalRecordActivity.this,
                            "Failed to save record: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}