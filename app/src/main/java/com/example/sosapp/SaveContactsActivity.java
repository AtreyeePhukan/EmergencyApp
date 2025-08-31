package com.example.sosapp;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SaveContactsActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private ArrayList<Contact> contactList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_contacts);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        recyclerView = findViewById(R.id.recyclerView);

        dbHelper = new DatabaseHelper(this);
        contactList = dbHelper.getAllContacts();

        adapter = new ContactAdapter(this, contactList, dbHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonAdd.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Enter both name and phone", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addContact(name, phone);
                contactList.add(new Contact(name, phone));
                adapter.notifyItemInserted(contactList.size() - 1);
                editTextName.setText("");
                editTextPhone.setText("");
                Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show();
            }
        });
    }
}




