package com.example.sosapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;
import java.util.ArrayList;

public class SendSMSService extends IntentService {


    public SendSMSService() {
        super("SendSMSService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        double latitude = intent.getDoubleExtra("latitude", 0.0);
        double longitude = intent.getDoubleExtra("longitude", 0.0);
        String address = intent.getStringExtra("address");

        String message = "🚨 Emergency! I need help.\n" +
                "📍 Address: " + address + "\n" +
                "🌐 Location: https://maps.google.com/?q=" + latitude + "," + longitude;

        Log.d("SOSApp", "Prepared message: " + message);

        DatabaseHelper db = new DatabaseHelper(this);
        ArrayList<Contact> contacts = db.getAllContacts();

        SmsManager smsManager = SmsManager.getDefault();

        try {
            for (Contact contact : contacts) {
                String number = contact.getPhone();  // Use getter from Contact class
                if (!number.isEmpty()) {
                    ArrayList<String> parts = smsManager.divideMessage(message);
                    smsManager.sendMultipartTextMessage(number, null, parts, null, null);
                    Log.d("SOSApp", "SMS sent to: " + number);
                }
            }
        } catch (Exception e) {
            Log.e("SOSApp", "Error sending SMS: " + e.getMessage());
        }
    }
}
