package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecordsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRecords;
    private TextView tvEmptyState;
    private MaterialButton btnAddRecord;

    private RecordAdapter adapter;
    private final List<MedicalRecord> recordList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_records);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();

        loadRecords();
    }

    private void initializeViews() {
        recyclerViewRecords = findViewById(R.id.recyclerViewRecords);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnAddRecord = findViewById(R.id.btnAddRecord);
    }

    private void setupRecyclerView() {
        recyclerViewRecords.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new RecordAdapter(recordList);
        recyclerViewRecords.setAdapter(adapter);
    }

    private void setupClickListeners() {

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        btnAddRecord.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MedicalRecordsActivity.this,
                    AddMedicalRecordActivity.class
            );
            startActivity(intent);
        });
    }

    private void loadRecords() {

        /*
         * Temporary empty state.
         *
         * Firestore medical_records collection will be connected
         * in the Firebase integration step.
         */

        recordList.clear();

        updateEmptyState();
    }

    private void updateEmptyState() {

        if (recordList.isEmpty()) {

            recyclerViewRecords.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);

        } else {

            recyclerViewRecords.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * Reload records whenever the user returns to this screen.
         * Later this will refresh the Firestore data.
         */
        loadRecords();
    }


    // ---------------------------------------------------------
    // Medical Record Model
    // ---------------------------------------------------------

    public static class MedicalRecord {

        private String recordType;
        private String title;
        private String details;

        public MedicalRecord(
                String recordType,
                String title,
                String details
        ) {
            this.recordType = recordType;
            this.title = title;
            this.details = details;
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
    }


    // ---------------------------------------------------------
    // RecyclerView Adapter
    // ---------------------------------------------------------

    private class RecordAdapter
            extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

        private final List<MedicalRecord> records;

        RecordAdapter(List<MedicalRecord> records) {
            this.records = records;
        }

        @Override
        public RecordViewHolder onCreateViewHolder(
                android.view.ViewGroup parent,
                int viewType
        ) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(
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

            MedicalRecord record = records.get(position);

            holder.tvRecordType.setText(record.getRecordType());
            holder.tvRecordTitle.setText(record.getTitle());
            holder.tvRecordDetails.setText(record.getDetails());

            holder.cardRecord.setOnClickListener(v -> {

                /*
                 * Edit/View functionality will be connected
                 * when AddMedicalRecordActivity is created.
                 */
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

            RecordViewHolder(View itemView) {
                super(itemView);

                cardRecord = itemView.findViewById(
                        R.id.cardRecord
                );

                tvRecordType = itemView.findViewById(
                        R.id.tvRecordType
                );

                tvRecordTitle = itemView.findViewById(
                        R.id.tvRecordTitle
                );

                tvRecordDetails = itemView.findViewById(
                        R.id.tvRecordDetails
                );
            }
        }
    }
}