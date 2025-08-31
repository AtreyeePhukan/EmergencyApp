package com.example.sosapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ResponseHandler {

    private JSONArray emergenciesArray;

    public ResponseHandler(Context context) {
        try {
            InputStream is = context.getAssets().open("emergencies.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject obj = new JSONObject(json);
            emergenciesArray = obj.getJSONArray("emergencies");
        } catch (Exception e) {
            Log.e("ResponseHandler", "Error loading JSON: " + e.getMessage());
        }
    }

    public String getBotResponse(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Please describe your emergency.";
        }

        userMessage = userMessage.toLowerCase(Locale.ROOT);

        try {
            for (int i = 0; i < emergenciesArray.length(); i++) {
                JSONObject emergency = emergenciesArray.getJSONObject(i);
                JSONArray keywords = emergency.getJSONArray("keywords");
                for (int j = 0; j < keywords.length(); j++) {
                    String keyword = keywords.getString(j).toLowerCase(Locale.ROOT);
                    if (userMessage.contains(keyword)) {
                        return emergency.getString("customReply");
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ResponseHandler", "Error matching emergency: " + e.getMessage());
        }

        return "Sorry, I couldn't recognize the emergency. Please try to describe it in simple words.";
    }
}
