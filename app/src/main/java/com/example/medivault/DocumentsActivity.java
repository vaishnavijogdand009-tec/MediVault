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

import java.util.ArrayList;
import java.util.List;

public class DocumentsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDocuments;
    private View tvEmptyState;
    private MaterialButton btnAddDocument;

    private final List<Document> documentList = new ArrayList<>();
    private DocumentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        loadDocuments();
    }

    private void initializeViews() {

        recyclerViewDocuments = findViewById(
                R.id.recyclerViewDocuments
        );

        tvEmptyState = findViewById(
                R.id.tvEmptyState
        );

        btnAddDocument = findViewById(
                R.id.btnAddDocument
        );
    }

    private void setupRecyclerView() {

        recyclerViewDocuments.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new DocumentAdapter(documentList);

        recyclerViewDocuments.setAdapter(adapter);
    }

    private void setupClickListeners() {

        findViewById(R.id.tvBack).setOnClickListener(
                v -> finish()
        );

        btnAddDocument.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DocumentsActivity.this,
                    AddDocumentActivity.class
            );

            startActivity(intent);
        });
    }

    private void loadDocuments() {

        /*
         * Firebase Storage + Firestore will be connected here.
         *
         * Currently the list is empty.
         */

        documentList.clear();

        updateEmptyState();
    }

    private void updateEmptyState() {

        if (documentList.isEmpty()) {

            recyclerViewDocuments.setVisibility(
                    View.GONE
            );

            tvEmptyState.setVisibility(
                    View.VISIBLE
            );

        } else {

            recyclerViewDocuments.setVisibility(
                    View.VISIBLE
            );

            tvEmptyState.setVisibility(
                    View.GONE
            );
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadDocuments();
    }


    // =========================================================
    // DOCUMENT MODEL
    // =========================================================

    public static class Document {

        private final String documentName;
        private final String documentType;

        public Document(
                String documentName,
                String documentType
        ) {
            this.documentName = documentName;
            this.documentType = documentType;
        }

        public String getDocumentName() {
            return documentName;
        }

        public String getDocumentType() {
            return documentType;
        }
    }


    // =========================================================
    // RECYCLER VIEW ADAPTER
    // =========================================================

    private class DocumentAdapter
            extends RecyclerView.Adapter<
            DocumentAdapter.DocumentViewHolder> {

        private final List<Document> documents;

        DocumentAdapter(List<Document> documents) {
            this.documents = documents;
        }

        @Override
        public DocumentViewHolder onCreateViewHolder(
                android.view.ViewGroup parent,
                int viewType
        ) {

            View view = LayoutInflater.from(
                    parent.getContext()
            ).inflate(
                    R.layout.activity_documents,
                    parent,
                    false
            );

            return new DocumentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                DocumentViewHolder holder,
                int position
        ) {

            Document document =
                    documents.get(position);

            holder.tvDocumentName.setText(
                    document.getDocumentName()
            );

            holder.tvDocumentType.setText(
                    document.getDocumentType()
            );

            holder.btnDelete.setOnClickListener(v -> {

                int currentPosition =
                        holder.getAdapterPosition();

                if (currentPosition !=
                        RecyclerView.NO_POSITION) {

                    documents.remove(
                            currentPosition
                    );

                    notifyItemRemoved(
                            currentPosition
                    );

                    updateEmptyState();

                    Toast.makeText(
                            DocumentsActivity.this,
                            "Document removed",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return documents.size();
        }


        // =====================================================
        // VIEW HOLDER
        // =====================================================

        class DocumentViewHolder
                extends RecyclerView.ViewHolder {

            MaterialCardView cardDocument;

            TextView tvDocumentName;
            TextView tvDocumentType;
            TextView btnDelete;

            DocumentViewHolder(View itemView) {

                super(itemView);

                cardDocument =
                        itemView.findViewById(
                                R.id.cardDocument
                        );

                tvDocumentName =
                        itemView.findViewById(
                                R.id.tvDocumentName
                        );

                tvDocumentType =
                        itemView.findViewById(
                                R.id.tvDocumentType
                        );

                btnDelete =
                        itemView.findViewById(
                                R.id.btnDelete
                        );
            }
        }
    }
}