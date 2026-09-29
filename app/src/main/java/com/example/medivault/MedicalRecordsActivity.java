package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MedicalRecordsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRecords;
    private View tvEmptyState;
    private MaterialButton btnAddRecord;

    private RecordAdapter adapter;

    private final List<MedicalRecord> recordList =
            new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_medical_records);

        initializeViews();

        firebaseAuth =
                FirebaseAuth.getInstance();

        firestore =
                FirebaseFirestore.getInstance();

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

        currentUserId =
                currentUser.getUid();

        setupRecyclerView();
        setupClickListeners();
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        recyclerViewRecords =
                findViewById(
                        R.id.recyclerViewRecords
                );

        tvEmptyState =
                findViewById(
                        R.id.tvEmptyState
                );

        btnAddRecord =
                findViewById(
                        R.id.btnAddRecord
                );
    }

    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private void setupRecyclerView() {

        recyclerViewRecords.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter =
                new RecordAdapter(recordList);

        recyclerViewRecords.setAdapter(
                adapter
        );
    }

    // =========================================================
    // CLICK LISTENERS
    // =========================================================

    private void setupClickListeners() {

        findViewById(R.id.tvBack)
                .setOnClickListener(v ->
                        finish()
                );

        btnAddRecord.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MedicalRecordsActivity.this,
                            AddMedicalRecordActivity.class
                    );

            startActivity(intent);
        });
    }

    // =========================================================
    // LOAD RECORDS FROM FIRESTORE
    // =========================================================

    private void loadRecords() {

        if (currentUserId == null) {
            return;
        }

        firestore
                .collection("medical_records")
                .whereEqualTo(
                        "userId",
                        currentUserId
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    recordList.clear();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        String recordId =
                                document.getString(
                                        "recordId"
                                );

                        String recordType =
                                document.getString(
                                        "recordType"
                                );

                        String title =
                                document.getString(
                                        "title"
                                );

                        String details =
                                document.getString(
                                        "details"
                                );

                        String documentUrl =
                                document.getString(
                                        "documentUrl"
                                );

                        Long createdAt =
                                document.getLong(
                                        "createdAt"
                                );

                        if (recordId == null) {
                            recordId =
                                    document.getId();
                        }

                        if (recordType == null) {
                            recordType = "Other";
                        }

                        if (title == null) {
                            title = "Untitled Record";
                        }

                        if (details == null) {
                            details = "";
                        }

                        if (documentUrl == null) {
                            documentUrl = "";
                        }

                        if (createdAt == null) {
                            createdAt = 0L;
                        }

                        MedicalRecord record =
                                new MedicalRecord(
                                        recordId,
                                        recordType,
                                        title,
                                        details,
                                        documentUrl,
                                        createdAt
                                );

                        recordList.add(record);
                    }

                    // Latest record first
                    Collections.sort(
                            recordList,
                            (record1, record2) ->
                                    Long.compare(
                                            record2.getCreatedAt(),
                                            record1.getCreatedAt()
                                    )
                    );

                    updateEmptyState();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            MedicalRecordsActivity.this,
                            "Failed to load records: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                    updateEmptyState();
                });
    }

    // =========================================================
    // EMPTY STATE
    // =========================================================

    private void updateEmptyState() {

        if (recordList.isEmpty()) {

            recyclerViewRecords.setVisibility(
                    View.GONE
            );

            tvEmptyState.setVisibility(
                    View.VISIBLE
            );

        } else {

            recyclerViewRecords.setVisibility(
                    View.VISIBLE
            );

            tvEmptyState.setVisibility(
                    View.GONE
            );
        }

        adapter.notifyDataSetChanged();
    }

    // =========================================================
    // REFRESH WHEN RETURNING
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (firebaseAuth != null &&
                firebaseAuth.getCurrentUser() != null) {

            loadRecords();
        }
    }

    // =========================================================
    // MEDICAL RECORD MODEL
    // =========================================================

    public static class MedicalRecord {

        private String recordId;
        private String recordType;
        private String title;
        private String details;
        private String documentUrl;
        private long createdAt;

        public MedicalRecord(
                String recordId,
                String recordType,
                String title,
                String details,
                String documentUrl,
                long createdAt
        ) {

            this.recordId = recordId;
            this.recordType = recordType;
            this.title = title;
            this.details = details;
            this.documentUrl = documentUrl;
            this.createdAt = createdAt;
        }

        public String getRecordId() {
            return recordId;
        }

        public String getRecordType() {
            return recordType;
        }

        public String getTitle() {
            return title;
        }

        public String getDetails() {
            return details;
        }

        public String getDocumentUrl() {
            return documentUrl;
        }

        public long getCreatedAt() {
            return createdAt;
        }
    }

    // =========================================================
    // RECYCLER VIEW ADAPTER
    // =========================================================

    private class RecordAdapter
            extends RecyclerView.Adapter<
            RecordAdapter.RecordViewHolder> {

        private final List<MedicalRecord> records;

        RecordAdapter(
                List<MedicalRecord> records
        ) {

            this.records = records;
        }

        @Override
        public RecordViewHolder onCreateViewHolder(
                android.view.ViewGroup parent,
                int viewType
        ) {

            View view =
                    LayoutInflater.from(
                            parent.getContext()
                    ).inflate(
                            R.layout.item_medical_record,
                            parent,
                            false
                    );

            return new RecordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                RecordViewHolder holder,
                int position
        ) {

            MedicalRecord record =
                    records.get(position);

            holder.tvRecordType.setText(
                    record.getRecordType()
            );

            holder.tvRecordTitle.setText(
                    record.getTitle()
            );

            holder.tvRecordDetails.setText(
                    record.getDetails()
            );

            holder.cardRecord.setOnClickListener(v -> {

                if (!record.getDocumentUrl().isEmpty()) {

                    Toast.makeText(
                            MedicalRecordsActivity.this,
                            "Document URL available",
                            Toast.LENGTH_SHORT
                    ).show();

                } else {

                    Toast.makeText(
                            MedicalRecordsActivity.this,
                            "No document attached",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }

        @Override
        public int getItemCount() {

            return records.size();
        }

        class RecordViewHolder
                extends RecyclerView.ViewHolder {

            MaterialCardView cardRecord;

            TextView tvRecordType;
            TextView tvRecordTitle;
            TextView tvRecordDetails;

            RecordViewHolder(
                    View itemView
            ) {

                super(itemView);

                cardRecord =
                        itemView.findViewById(
                                R.id.cardRecord
                        );

                tvRecordType =
                        itemView.findViewById(
                                R.id.tvRecordType
                        );

                tvRecordTitle =
                        itemView.findViewById(
                                R.id.tvRecordTitle
                        );

                tvRecordDetails =
                        itemView.findViewById(
                                R.id.tvRecordDetails
                        );
            }
        }
    }
}