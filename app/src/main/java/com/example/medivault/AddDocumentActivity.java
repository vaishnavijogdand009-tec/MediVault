package com.example.medivault;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddDocumentActivity extends AppCompatActivity {

    private TextView tvBack;
    private TextView tvSelectedFile;
    private AutoCompleteTextView etDocumentType;
    private TextInputEditText etDocumentName;
    private MaterialButton btnChooseFile;
    private MaterialButton btnSaveDocument;

    private Uri selectedFileUri;

    private final String[] documentTypes = {
            "Medical Report",
            "Prescription",
            "Lab Report",
            "X-Ray",
            "Scan",
            "Vaccination Certificate",
            "Discharge Summary",
            "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);

        initializeViews();
        setupDocumentTypeDropdown();
        setupClickListeners();
    }

    private void initializeViews() {

        tvBack = findViewById(R.id.tvBack);
        tvSelectedFile = findViewById(R.id.tvSelectedFile);

        etDocumentType = findViewById(R.id.etDocumentType);
        etDocumentName = findViewById(R.id.etDocumentName);

        btnChooseFile = findViewById(R.id.btnChooseFile);
        btnSaveDocument = findViewById(R.id.btnSaveDocument);
    }

    private void setupDocumentTypeDropdown() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                documentTypes
        );

        etDocumentType.setAdapter(adapter);

        etDocumentType.setOnClickListener(v ->
                etDocumentType.showDropDown()
        );
    }

    private void setupClickListeners() {

        tvBack.setOnClickListener(v -> finish());

        btnChooseFile.setOnClickListener(v ->
                chooseDocument()
        );

        btnSaveDocument.setOnClickListener(v ->
                saveDocument()
        );
    }

    private void chooseDocument() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("*/*");

        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == 100 &&
                resultCode == RESULT_OK &&
                data != null) {

            selectedFileUri = data.getData();

            if (selectedFileUri != null) {

                String fileName =
                        getFileName(selectedFileUri);

                tvSelectedFile.setText(
                        "Selected: " + fileName
                );

                tvSelectedFile.setTextColor(
                        getResources().getColor(
                                android.R.color.holo_green_dark
                        )
                );

                if (TextUtils.isEmpty(
                        etDocumentName.getText().toString().trim()
                )) {
                    etDocumentName.setText(fileName);
                }
            }
        }
    }

    private String getFileName(Uri uri) {

        String result = null;

        if ("content".equals(uri.getScheme())) {

            try (Cursor cursor = getContentResolver()
                    .query(
                            uri,
                            null,
                            null,
                            null,
                            null
                    )) {

                if (cursor != null &&
                        cursor.moveToFirst()) {

                    int index = cursor.getColumnIndex(
                            OpenableColumns.DISPLAY_NAME
                    );

                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }

        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result != null ? result : "Selected document";
    }

    private void saveDocument() {

        String documentType =
                etDocumentType.getText()
                        .toString()
                        .trim();

        String documentName =
                etDocumentName.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(documentType)) {

            etDocumentType.setError(
                    "Please select document type"
            );

            etDocumentType.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(documentName)) {

            etDocumentName.setError(
                    "Please enter document name"
            );

            etDocumentName.requestFocus();
            return;
        }

        if (selectedFileUri == null) {

            Toast.makeText(
                    this,
                    "Please choose a document",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        /*
         * Firebase Storage integration will be added later.
         *
         * File:
         * selectedFileUri
         *
         * Firestore document:
         *
         * documentId
         * userId
         * documentName
         * documentType
         * documentUrl
         * createdAt
         */

        Toast.makeText(
                this,
                "Document ready to upload",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}