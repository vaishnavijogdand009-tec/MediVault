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

public class AddMedicalRecordActivity extends AppCompatActivity {

    private AutoCompleteTextView etRecordType;
    private TextInputEditText etRecordTitle;
    private TextInputEditText etRecordDetails;
    private TextInputEditText etDocumentUrl;

    private MaterialButton btnSaveRecord;
    private TextView tvBack;

    private String[] recordTypes = {
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
        setupRecordTypeDropdown();
        setupClickListeners();
    }

    private void initializeViews() {

        tvBack = findViewById(R.id.tvBack);

        etRecordType = findViewById(R.id.etRecordType);
        etRecordTitle = findViewById(R.id.etRecordTitle);
        etRecordDetails = findViewById(R.id.etRecordDetails);
        etDocumentUrl = findViewById(R.id.etDocumentUrl);

        btnSaveRecord = findViewById(R.id.btnSaveRecord);
    }

    private void setupRecordTypeDropdown() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                recordTypes
        );

        etRecordType.setAdapter(adapter);

        etRecordType.setOnClickListener(v ->
                etRecordType.showDropDown()
        );
    }

    private void setupClickListeners() {

        tvBack.setOnClickListener(v -> finish());

        btnSaveRecord.setOnClickListener(v -> saveMedicalRecord());
    }

    private void saveMedicalRecord() {

        String recordType = etRecordType.getText()
                .toString()
                .trim();

        String title = etRecordTitle.getText()
                .toString()
                .trim();

        String details = etRecordDetails.getText()
                .toString()
                .trim();

        String documentUrl = etDocumentUrl.getText()
                .toString()
                .trim();


        // Record type validation
        if (TextUtils.isEmpty(recordType)) {

            etRecordType.setError("Please select a record type");
            etRecordType.requestFocus();
            return;
        }


        // Title validation
        if (TextUtils.isEmpty(title)) {

            etRecordTitle.setError("Please enter record title");
            etRecordTitle.requestFocus();
            return;
        }


        // Details validation
        if (TextUtils.isEmpty(details)) {

            etRecordDetails.setError("Please enter medical details");
            etRecordDetails.requestFocus();
            return;
        }


        /*
         * Firebase Firestore will be connected here later.
         *
         * The record will contain:
         * recordId
         * userId
         * recordType
         * title
         * details
         * documentUrl
         * createdAt
         */

        Toast.makeText(
                this,
                "Medical record saved successfully",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}