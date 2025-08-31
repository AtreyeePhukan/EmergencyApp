package com.example.sosapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private final ArrayList<Contact> contactList;
    private final DatabaseHelper dbHelper;
    private final Context context;

    public ContactAdapter(Context context, ArrayList<Contact> contactList, DatabaseHelper dbHelper) {
        this.context = context;
        this.contactList = contactList;
        this.dbHelper = dbHelper;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName, textViewPhone;
        public AppCompatImageButton buttonDelete;
        public ViewHolder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.textViewName);
            textViewPhone = view.findViewById(R.id.textViewPhone);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);

        }
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.textViewName.setText(contact.getName());
        holder.textViewPhone.setText(contact.getPhone());

        holder.buttonDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            Contact contactToDelete = contactList.get(pos);

            dbHelper.deleteContact(contactToDelete.getName(), contactToDelete.getPhone());

            contactList.remove(pos);
            notifyItemRemoved(pos);

            Toast.makeText(context, "Contact Deleted", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
