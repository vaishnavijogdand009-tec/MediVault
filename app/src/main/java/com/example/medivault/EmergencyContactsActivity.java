package com.example.medivault;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class EmergencyContactsActivity extends AppCompatActivity {

    private TextInputEditText etContactName;
    private TextInputEditText etContactPhone;
    private TextInputEditText etRelationship;

    private MaterialButton btnAddContact;
    private RecyclerView recyclerViewContacts;
    private TextView tvEmptyState;

    private final List<EmergencyContact> contactList = new ArrayList<>();
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        updateEmptyState();
    }

    private void initializeViews() {

        etContactName = findViewById(R.id.etContactName);
        etContactPhone = findViewById(R.id.etContactPhone);
        etRelationship = findViewById(R.id.etRelationship);

        btnAddContact = findViewById(R.id.btnAddContact);
        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        tvEmptyState = findViewById(R.id.tvEmptyState);
    }

    private void setupRecyclerView() {

        recyclerViewContacts.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new ContactAdapter(contactList);
        recyclerViewContacts.setAdapter(adapter);
    }

    private void setupClickListeners() {

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        btnAddContact.setOnClickListener(v -> addContact());
    }

    private void addContact() {

        String name = etContactName.getText()
                .toString()
                .trim();

        String phone = etContactPhone.getText()
                .toString()
                .trim();

        String relationship = etRelationship.getText()
                .toString()
                .trim();


        if (TextUtils.isEmpty(name)) {
            etContactName.setError("Please enter contact name");
            etContactName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etContactPhone.setError("Please enter phone number");
            etContactPhone.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            etContactPhone.setError("Please enter a valid phone number");
            etContactPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(relationship)) {
            etRelationship.setError("Please enter relationship");
            etRelationship.requestFocus();
            return;
        }


        /*
         * Firebase Firestore will be connected here later.
         *
         * Collection:
         * emergency_contacts
         *
         * Fields:
         * contactId
         * userId
         * name
         * phone
         * relationship
         * createdAt
         */

        EmergencyContact contact = new EmergencyContact(
                name,
                phone,
                relationship
        );

        contactList.add(contact);

        adapter.notifyItemInserted(contactList.size() - 1);

        updateEmptyState();

        clearFields();

        Toast.makeText(
                this,
                "Emergency contact added",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void clearFields() {

        etContactName.setText("");
        etContactPhone.setText("");
        etRelationship.setText("");

        etContactName.requestFocus();
    }

    private void updateEmptyState() {

        if (contactList.isEmpty()) {

            recyclerViewContacts.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);

        } else {

            recyclerViewContacts.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }


    // ---------------------------------------------------------
    // Emergency Contact Model
    // ---------------------------------------------------------

    public static class EmergencyContact {

        private String name;
        private String phone;
        private String relationship;

        public EmergencyContact(
                String name,
                String phone,
                String relationship
        ) {
            this.name = name;
            this.phone = phone;
            this.relationship = relationship;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getRelationship() {
            return relationship;
        }
    }


    // ---------------------------------------------------------
    // RecyclerView Adapter
    // ---------------------------------------------------------

    private class ContactAdapter
            extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

        private final List<EmergencyContact> contacts;

        ContactAdapter(List<EmergencyContact> contacts) {
            this.contacts = contacts;
        }

        @Override
        public ContactViewHolder onCreateViewHolder(
                android.view.ViewGroup parent,
                int viewType
        ) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(
                            R.layout.item_emergency_contact,
                            parent,
                            false
                    );

            return new ContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                ContactViewHolder holder,
                int position
        ) {

            EmergencyContact contact = contacts.get(position);

            holder.tvContactName.setText(
                    contact.getName()
            );

            holder.tvContactPhone.setText(
                    contact.getPhone()
            );

            holder.tvRelationship.setText(
                    contact.getRelationship()
            );


            holder.btnDelete.setOnClickListener(v -> {

                int currentPosition = holder.getAdapterPosition();

                if (currentPosition != RecyclerView.NO_POSITION) {

                    contacts.remove(currentPosition);

                    notifyItemRemoved(currentPosition);

                    updateEmptyState();

                    Toast.makeText(
                            EmergencyContactsActivity.this,
                            "Contact removed",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        class ContactViewHolder
                extends RecyclerView.ViewHolder {

            MaterialCardView cardContact;
            TextView tvContactName;
            TextView tvContactPhone;
            TextView tvRelationship;
            TextView btnDelete;

            ContactViewHolder(View itemView) {
                super(itemView);

                cardContact = itemView.findViewById(
                        R.id.cardContact
                );

                tvContactName = itemView.findViewById(
                        R.id.tvContactName
                );

                tvContactPhone = itemView.findViewById(
                        R.id.tvContactPhone
                );

                tvRelationship = itemView.findViewById(
                        R.id.tvRelationship
                );

                btnDelete = itemView.findViewById(
                        R.id.btnDelete
                );
            }
        }
    }
}