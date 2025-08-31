package com.example.sosapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.isUser()) {
            holder.userText.setVisibility(View.VISIBLE);
            holder.botText.setVisibility(View.GONE);
            holder.userText.setText(message.getText());
        } else {
            holder.botText.setVisibility(View.VISIBLE);
            holder.userText.setVisibility(View.GONE);
            holder.botText.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView userText;
        TextView botText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userText = itemView.findViewById(R.id.userText);
            botText = itemView.findViewById(R.id.botText);
        }
    }
}

