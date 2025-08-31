package com.example.sosapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.telecom.TelecomManager;
import android.widget.Toast;
import java.lang.reflect.Method;

public class FakeCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Incoming Fake Call", Toast.LENGTH_LONG).show();
        playRingtone(context);
        triggerFakeCallScreen(context);
    }

    private void playRingtone(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.start();
        new Handler().postDelayed(mediaPlayer::stop, 5000);
    }

    private void triggerFakeCallScreen(Context context) {
        try {
            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            Method method = telecomManager.getClass().getDeclaredMethod("silenceRinger");
            method.invoke(telecomManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
