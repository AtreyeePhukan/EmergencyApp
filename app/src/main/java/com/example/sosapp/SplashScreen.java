package com.example.sosapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            DatabaseHandler dbHandler = new DatabaseHandler(SplashScreen.this);
            boolean isRegistered = dbHandler.isRegistered();
            if (isRegistered) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashScreen.this, RegisterActivity.class));
            }
            finish();
        }, SPLASH_DURATION);
    }
}

