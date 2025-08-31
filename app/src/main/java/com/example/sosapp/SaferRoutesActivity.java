package com.example.sosapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SaferRoutesActivity extends AppCompatActivity {
    private MapView map;
    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_PERMISSION_CODE = 1001;
    private OkHttpClient client = new OkHttpClient();

    private final String OPENROUTESERVICE_API_KEY = BuildConfig.OPENROUTESERVICE_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));

        setContentView(R.layout.activity_safer_routes);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        } else {
            getUserLocation();
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double currentLat = location.getLatitude();
                        double currentLon = location.getLongitude();

                        GeoPoint currentPoint = new GeoPoint(currentLat, currentLon);
                        map.getController().setZoom(16.0);
                        map.getController().setCenter(currentPoint);

                        Marker currentMarker = new Marker(map);
                        currentMarker.setPosition(currentPoint);
                        currentMarker.setTitle("You are here");
                        map.getOverlays().add(currentMarker);

                        getNearbyPoliceStations(currentLat, currentLon);
                        getNearbyCrowdedAreas(currentLat, currentLon);
                    } else {
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getNearbyPoliceStations(double latitude, double longitude) {
        String overpassUrl = "https://overpass-api.de/api/interpreter?data=[out:json];(node[amenity=police](around:5000," + latitude + "," + longitude + "););out;";

        Request request = new Request.Builder().url(overpassUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SaferRoutesActivity.this, "Failed to fetch police stations", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(SaferRoutesActivity.this, "Error fetching police stations", Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    JSONArray elements = jsonResponse.getJSONArray("elements");

                    if (elements.length() > 0) {
                        JSONObject policeStation = elements.getJSONObject(0);
                        double lat = policeStation.getDouble("lat");
                        double lon = policeStation.getDouble("lon");

                        runOnUiThread(() -> {
                            GeoPoint policePoint = new GeoPoint(lat, lon);
                            Marker policeMarker = new Marker(map);
                            policeMarker.setPosition(policePoint);
                            policeMarker.setTitle("Nearest Police Station");
                            map.getOverlays().add(policeMarker);

                            fetchRoute(latitude, longitude, lat, lon);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getNearbyCrowdedAreas(double latitude, double longitude) {
        String overpassUrl = "https://overpass-api.de/api/interpreter?data=[out:json];(node[amenity=cafe](around:5000," + latitude + "," + longitude + ");node[amenity=restaurant](around:5000," + latitude + "," + longitude + ");node[amenity=marketplace](around:5000," + latitude + "," + longitude + ");node[highway=bus_stop](around:5000," + latitude + "," + longitude + "););out;";

        Request request = new Request.Builder().url(overpassUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SaferRoutesActivity.this, "Failed to fetch crowded areas", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(SaferRoutesActivity.this, "Error fetching crowded areas", Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    JSONArray elements = jsonResponse.getJSONArray("elements");

                    runOnUiThread(() -> {
                        for (int i = 0; i < elements.length(); i++) {
                            try {
                                JSONObject element = elements.getJSONObject(i);
                                double lat = element.getDouble("lat");
                                double lon = element.getDouble("lon");

                                // Load the marker image from resources
                                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_red);

                                // Resize the bitmap (e.g., resize it to 80x80)
                                int newWidth = 80;  // Desired width
                                int newHeight = 80; // Desired height
                                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);

                                // Create a new GeoPoint for the crowded area
                                GeoPoint crowdedPoint = new GeoPoint(lat, lon);
                                Marker crowdedMarker = new Marker(map);
                                crowdedMarker.setPosition(crowdedPoint);
                                crowdedMarker.setTitle("Crowded Area");

                                // Set the resized bitmap as the marker's icon
                                crowdedMarker.setIcon(new BitmapDrawable(getResources(), resizedBitmap));

                                // Add the marker to the map
                                map.getOverlays().add(crowdedMarker);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fetchRoute(double startLat, double startLon, double endLat, double endLon) {
        String url = "https://api.openrouteservice.org/v2/directions/foot-walking/geojson";

        JSONObject body = new JSONObject();
        try {
            JSONArray coords = new JSONArray();
            coords.put(new JSONArray(new double[]{startLon, startLat}));
            coords.put(new JSONArray(new double[]{endLon, endLat}));
            body.put("coordinates", coords);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody requestBody = RequestBody.create(body.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", OPENROUTESERVICE_API_KEY)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SaferRoutesActivity.this, "Failed to fetch route", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(SaferRoutesActivity.this, "Route fetch error", Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    JSONObject resObj = new JSONObject(response.body().string());
                    JSONArray coordinates = resObj.getJSONArray("features")
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONArray("coordinates");

                    ArrayList<GeoPoint> routePoints = new ArrayList<>();
                    for (int i = 0; i < coordinates.length(); i++) {
                        JSONArray point = coordinates.getJSONArray(i);
                        double lon = point.getDouble(0);
                        double lat = point.getDouble(1);
                        routePoints.add(new GeoPoint(lat, lon));
                    }

                    runOnUiThread(() -> {
                        Polyline routeLine = new Polyline();
                        routeLine.setPoints(routePoints);
                        routeLine.setColor(Color.GREEN);
                        routeLine.setWidth(8f);
                        map.getOverlays().add(routeLine);
                        map.invalidate();
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
