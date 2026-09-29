package com.example.medivault;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddDocumentActivity extends AppCompatActivity {

    private TextView tvBack;
    private TextView tvSelectedFile;

    private AutoCompleteTextView etDocumentType;
    private TextInputEditText etDocumentName;

    private MaterialButton btnChooseFile;
    private MaterialButton btnSaveDocument;

    private Uri selectedFileUri;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    private String currentUserId;

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

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

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

        setupDocumentTypeDropdown();
        setupClickListeners();
    }

    private void initializeViews() {

        tvBack = findViewById(R.id.tvBack);
        tvSelectedFile = findViewById(R.id.tvSelectedFile);

        etDocumentType =
                findViewById(R.id.etDocumentType);

        etDocumentName =
                findViewById(R.id.etDocumentName);

        btnChooseFile =
                findViewById(R.id.btnChooseFile);

        btnSaveDocument =
                findViewById(R.id.btnSaveDocument);
    }

    private void setupDocumentTypeDropdown() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
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

    // =========================================================
    // CHOOSE DOCUMENT
    // =========================================================

    private void chooseDocument() {

        Intent intent =
                new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        intent.setType("*/*");

        startActivityForResult(
                intent,
                100
        );
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

                // Keep permission for this URI
                try {

                    getContentResolver()
                            .takePersistableUriPermission(
                                    selectedFileUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );

                } catch (Exception ignored) {
                }

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
                        etDocumentName
                                .getText()
                                .toString()
                                .trim()
                )) {

                    etDocumentName.setText(
                            fileName
                    );
                }
            }
        }
    }

    // =========================================================
    // GET FILE NAME
    // =========================================================

    private String getFileName(Uri uri) {

        String result = null;

        if ("content".equals(
                uri.getScheme()
        )) {

            try (Cursor cursor =
                         getContentResolver().query(
                                 uri,
                                 null,
                                 null,
                                 null,
                                 null
                         )) {

                if (cursor != null &&
                        cursor.moveToFirst()) {

                    int index =
                            cursor.getColumnIndex(
                                    OpenableColumns.DISPLAY_NAME
                            );

                    if (index >= 0) {

                        result =
                                cursor.getString(index);
                    }
                }
            }
        }

        if (result == null) {

            result =
                    uri.getLastPathSegment();
        }

        return result != null
                ? result
                : "Selected document";
    }

    // =========================================================
    // SAVE DOCUMENT
    // =========================================================

    private void saveDocument() {

        String documentType =
                etDocumentType
                        .getText()
                        .toString()
                        .trim();

        String documentName =
                etDocumentName
                        .getText()
                        .toString()
                        .trim();

        // -----------------------------
        // VALIDATION
        // -----------------------------

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

        // -----------------------------
        // DISABLE BUTTON
        // -----------------------------

        btnSaveDocument.setEnabled(false);
        btnSaveDocument.setText(
                "Uploading..."
        );

        // -----------------------------
        // STORAGE PATH
        // -----------------------------

        String fileName =
                System.currentTimeMillis()
                        + "_"
                        + getFileName(selectedFileUri);

        StorageReference documentReference =
                storage.getReference()
                        .child("medical_documents")
                        .child(currentUserId)
                        .child(fileName);

        // -----------------------------
        // UPLOAD TO FIREBASE STORAGE
        // -----------------------------

        documentReference
                .putFile(selectedFileUri)
                .addOnSuccessListener(taskSnapshot -> {

                    // Get download URL
                    documentReference
                            .getDownloadUrl()
                            .addOnSuccessListener(uri -> {

                                String documentUrl =
                                        uri.toString();

                                saveDocumentToFirestore(
                                        documentName,
                                        documentType,
                                        documentUrl,
                                        fileName
                                );

                            })
                            .addOnFailureListener(e -> {

                                uploadFailed(
                                        "Unable to get document URL: "
                                                + e.getMessage()
                                );
                            });

                })
                .addOnFailureListener(e -> {

                    uploadFailed(
                            "Document upload failed: "
                                    + e.getMessage()
                    );
                });
    }

    // =========================================================
    // SAVE DOCUMENT METADATA TO FIRESTORE
    // =========================================================

    private void saveDocumentToFirestore(
            String documentName,
            String documentType,
            String documentUrl,
            String storageFileName
    ) {

        String documentId =
                firestore
                        .collection("documents")
                        .document()
                        .getId();

        Map<String, Object> documentData =
                new HashMap<>();

        documentData.put(
                "documentId",
                documentId
        );

        documentData.put(
                "userId",
                currentUserId
        );

        documentData.put(
                "documentName",
                documentName
        );

        documentData.put(
                "documentType",
                documentType
        );

        documentData.put(
                "documentUrl",
                documentUrl
        );

        documentData.put(
                "storageFileName",
                storageFileName
        );

        documentData.put(
                "createdAt",
                System.currentTimeMillis()
        );

        firestore
                .collection("documents")
                .document(documentId)
                .set(documentData)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            AddDocumentActivity.this,
                            "Document saved successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    btnSaveDocument.setEnabled(true);

                    btnSaveDocument.setText(
                            "Save Document"
                    );

                    Toast.makeText(
                            AddDocumentActivity.this,
                            "File uploaded but Firestore save failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // UPLOAD FAILED
    // =========================================================

    private void uploadFailed(String message) {

        btnSaveDocument.setEnabled(true);

        btnSaveDocument.setText(
                "Save Document"
        );

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}