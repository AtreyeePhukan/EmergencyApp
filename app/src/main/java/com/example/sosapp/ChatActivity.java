package com.example.sosapp;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.speech.RecognizerIntent;
import android.content.Intent;
import android.app.Activity;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText userInput;
    private MessageAdapter adapter;
    private List<Message> messages;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    private ResponseHandler responseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.recyclerView);
        userInput = findViewById(R.id.editTextMessage);
        ImageButton sendButton = findViewById(R.id.buttonSend);

        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(adapter);
        responseHandler = new ResponseHandler(this);

        ImageButton voiceButton = findViewById(R.id.voiceButton);

        voiceButton.setOnClickListener(view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak emergency...");
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (Exception e) {
                Toast.makeText(this, "Voice not supported", Toast.LENGTH_SHORT).show();
            }
        });


        sendButton.setOnClickListener(v -> {
            String input = userInput.getText().toString().trim();
            if (!input.isEmpty()) {
                messages.add(new Message(input, true));
                String reply = responseHandler.getBotResponse(input);
                messages.add(new Message(reply, false));
                adapter.notifyItemRangeInserted(messages.size() - 2, 2);
                chatRecyclerView.scrollToPosition(messages.size() - 1);
                userInput.setText("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                userInput.setText(spokenText);
                messages.add(new Message(spokenText, true));
                ResponseHandler handler = new ResponseHandler(this);
                String reply = handler.getBotResponse(spokenText);
                messages.add(new Message(reply, false));
                adapter.notifyItemRangeInserted(messages.size() - 2, 2);
                chatRecyclerView.scrollToPosition(messages.size() - 1);
            }
        }
    }

}
