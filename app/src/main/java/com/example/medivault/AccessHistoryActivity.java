package com.example.medivault;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccessHistoryActivity extends AppCompatActivity {

    private TextView tvBack;
    private TextView tvEmptyState;
    private RecyclerView recyclerViewHistory;

    private HistoryAdapter adapter;
    private final List<AccessLog> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_history);

        initializeViews();
        setupRecyclerView();

        tvBack.setOnClickListener(v -> finish());

        loadAccessHistory();
    }

    private void initializeViews() {

        tvBack = findViewById(R.id.tvBack);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
    }

    private void setupRecyclerView() {

        recyclerViewHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new HistoryAdapter(historyList);

        recyclerViewHistory.setAdapter(adapter);
    }

    private void loadAccessHistory() {

        historyList.clear();

        /*
         * Sample data for the prototype.
         *
         * Firebase Firestore access_logs collection
         * will be connected later.
         */

        String currentTime =
                new SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a",
                        Locale.getDefault()
                ).format(new Date());

        historyList.add(
                new AccessLog(
                        "Emergency QR Access",
                        "Emergency information viewed",
                        currentTime,
                        "QR Scan"
                )
        );

        updateEmptyState();
    }

    private void updateEmptyState() {

        if (historyList.isEmpty()) {

            recyclerViewHistory.setVisibility(
                    View.GONE
            );

            tvEmptyState.setVisibility(
                    View.VISIBLE
            );

        } else {

            recyclerViewHistory.setVisibility(
                    View.VISIBLE
            );

            tvEmptyState.setVisibility(
                    View.GONE
            );
        }

        adapter.notifyDataSetChanged();
    }

    public static class AccessLog {

        private final String title;
        private final String description;
        private final String dateTime;
        private final String accessType;

        public AccessLog(
                String title,
                String description,
                String dateTime,
                String accessType
        ) {

            this.title = title;
            this.description = description;
            this.dateTime = dateTime;
            this.accessType = accessType;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getDateTime() {
            return dateTime;
        }

        public String getAccessType() {
            return accessType;
        }
    }

    private class HistoryAdapter
            extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

        private final List<AccessLog> logs;

        HistoryAdapter(List<AccessLog> logs) {
            this.logs = logs;
        }

        @Override
        public HistoryViewHolder onCreateViewHolder(
                android.view.ViewGroup parent,
                int viewType
        ) {

            View view = LayoutInflater.from(
                    parent.getContext()
            ).inflate(
                    R.layout.item_access_history,
                    parent,
                    false
            );

            return new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                HistoryViewHolder holder,
                int position
        ) {

            AccessLog log = logs.get(position);

            holder.tvTitle.setText(
                    log.getTitle()
            );

            holder.tvDescription.setText(
                    log.getDescription()
            );

            holder.tvDateTime.setText(
                    log.getDateTime()
            );

            holder.tvAccessType.setText(
                    log.getAccessType()
            );
        }

        @Override
        public int getItemCount() {
            return logs.size();
        }

        class HistoryViewHolder
                extends RecyclerView.ViewHolder {

            MaterialCardView cardHistory;

            TextView tvTitle;
            TextView tvDescription;
            TextView tvDateTime;
            TextView tvAccessType;

            HistoryViewHolder(View itemView) {

                super(itemView);

                cardHistory =
                        itemView.findViewById(
                                R.id.cardHistory
                        );

                tvTitle =
                        itemView.findViewById(
                                R.id.tvTitle
                        );

                tvDescription =
                        itemView.findViewById(
                                R.id.tvDescription
                        );

                tvDateTime =
                        itemView.findViewById(
                                R.id.tvDateTime
                        );

                tvAccessType =
                        itemView.findViewById(
                                R.id.tvAccessType
                        );
            }
        }
    }
}