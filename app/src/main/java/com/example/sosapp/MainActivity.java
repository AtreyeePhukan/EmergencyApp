package com.example.sosapp;


import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;





public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int REQUEST_PERMISSIONS = 1;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    //private MediaRecorder mediaRecorder;
    private float lastX, lastY, lastZ;
    private long lastUpdate;
    private static final int SHAKE_THRESHOLD = 800;
    //private String audioFilePath;
    private SharedPreferences contactsPref;
    boolean isFakeCallRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnEmergency = findViewById(R.id.btnAlert);
        Button btnFakeCall = findViewById(R.id.btnFakeCall);
        Button btnSaveContacts = findViewById(R.id.btnSaveContacts);
        Button btnSaferRoutes = findViewById(R.id.btnSafeRoutes);
        Button btnLocation = findViewById(R.id.btnTrackLocation);
        Button openChatButton = findViewById(R.id.openChatButton);

        contactsPref = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        btnEmergency.setOnClickListener(v -> sendEmergencyAlert());
        btnFakeCall.setOnClickListener(v -> scheduleFakeCall());
        btnSaveContacts.setOnClickListener(v -> startActivity(new Intent(this, SaveContactsActivity.class)));
//        btnLocation.setOnClickListener(v -> startLocationTracking());
        btnSaferRoutes.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SaferRoutesActivity.class)));


        btnLocation.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:0,0?q=my+location"));
            startActivity(intent);
        });
//        btnFakeCall.setOnClickListener(view -> {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//                makeFakeCall();
//            } else {
//                isFakeCallRequested = true;
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSIONS);
//            }
//        });

        openChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        TextView loginTextView = findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        checkPermissions();
    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CALL_PHONE
        };
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    Toast.makeText(this, "Permission not granted: " + permissions[i], Toast.LENGTH_SHORT).show();
                }
            }
            if (allGranted && isFakeCallRequested) {
                makeFakeCall();
                isFakeCallRequested = false;
            }
        }
    }

    // Step 4: Fake Call method
    private void makeFakeCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:6001062618"));
        startActivity(callIntent);
    }

    private void sendEmergencyAlert() {
        Location location = getLastKnownLocation();
        if (location != null) {
            String addressText = "Unknown Location";
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    addressText = address.getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent smsIntent = new Intent(MainActivity.this, SendSMSService.class);
            smsIntent.putExtra("latitude", location.getLatitude());
            smsIntent.putExtra("longitude", location.getLongitude());
            smsIntent.putExtra("address", addressText);
            startService(smsIntent);

            Toast.makeText(this, "Emergency alert triggered!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Location not available!", Toast.LENGTH_SHORT).show();
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (gpsLocation != null) return gpsLocation;
            if (networkLocation != null) return networkLocation;
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
        }

        return null;
    }



    private void scheduleFakeCall() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, FakeCallReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        Toast.makeText(this, "Fake call scheduled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        long curTime = System.currentTimeMillis();
        if ((curTime - lastUpdate) > 100) {
            long diffTime = curTime - lastUpdate;
            lastUpdate = curTime;
            float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;
            if (speed > SHAKE_THRESHOLD) {
                sendEmergencyAlert();
            }
            lastX = x;
            lastY = y;
            lastZ = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void startLocationTracking() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation != null) {
            double latitude = lastLocation.getLatitude();
            double longitude = lastLocation.getLongitude();
            Toast.makeText(this, "Current Location:\nLat: " + latitude + "\nLon: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
        }


        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Toast.makeText(getApplicationContext(), "https://maps.google.com/?q=" + latitude + "," + longitude , Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "Updated Location:\nLat: " + latitude + "\nLon: " + longitude, Toast.LENGTH_LONG).show();
            }

            @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override public void onProviderEnabled(@NonNull String provider) {}
            @Override public void onProviderDisabled(@NonNull String provider) {}
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener); // every 10 seconds or 10 meters
    }

}