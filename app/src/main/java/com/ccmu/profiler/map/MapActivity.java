package com.ccmu.profiler.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ccmu.profiler.R;
import com.ccmu.profiler.ui.edit.EditUserDataActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String HOME_LOCATION_DATA_INTEND_ID = "home_location_data_intent_id";
    public static final String WORK_LOCATION_DATA_INTENT_ID = "work_location_data_intent_id";
    public static final String GET_LOCATION = "get_location";

    public static final String USER_LOCATION_DATA = "user_location_data.txt";
    public static final String HOME_LOCATION_FORMAT = "HOME_LOCATION";
    public static final String WORK_LOCATION_FORMAT = "WORK_LOCATION";

    private static final int LOCATION_REQUEST_CODE = 20;
    private Marker lastMarker;
    private LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

//        if (ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission
//                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.d("PERMISSION - OnCreate", "asking for permission ACCESS_FINE_LOCATION");
//            ActivityCompat.requestPermissions(this, new String[]{Context.LOCATION_SERVICE}, LOCATION_REQUEST_CODE);
//        }

        Log.d("OnCreate MapActivity", "initializing supportMapFragment");
        SupportMapFragment supportMapFragment = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment));
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }
        Log.d("SupportMapFragment STATUS", (supportMapFragment == null) ? "null" : "instantiated");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setOnMapClickListener(latLng -> {
            Log.d("Map READY", "Map clicked");
            googleMap.clear();
            lastMarker = googleMap.addMarker(new MarkerOptions().draggable(true).position(latLng));
        });
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("OnMapReady", "Permission are granted");

            CurrentLocation.LocationResult locationResult = new CurrentLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    LatLng here = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d("OnMapReady - LocationResult CallBack", "Got \"here\" location");
                    googleMap.addMarker(new MarkerOptions()
                            .position(here)
                            .title("Your position"));
                    Log.d("OnMapReady - LocationResult CallBack", "Marker added");
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(here));
                    Log.d("OnMapReady - LocationResult CallBack", "Camera moved");
                    Log.d("LocationResult - OnMapReady - LocationResult CallBack", "Got location");
                    Log.d("GoogleMap instantiated?", googleMap != null ? "true" : "false");
                }
            };
            CurrentLocation currentLocation = new CurrentLocation();
            currentLocation.getLocation(this, locationResult);
        } else if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSION - OnMapReady", "asking for permission ACCESS_FINE_LOCATION");
            ActivityCompat.requestPermissions(this, new String[]{Context.LOCATION_SERVICE}, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("OnRequestPermissionsResult", "Result received");
        if (requestCode == LOCATION_REQUEST_CODE && grantResults[0] == RESULT_OK) {
            Log.d("OnRequestPermissionsResult", "Permissions GRANTED");
            locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        }
        Log.d("OnRequestPermissionsResult", "Result handled");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("OnNewIntent MapActivity", "Intent is arrived");
        if (Objects.equals(intent.getStringExtra(GET_LOCATION), HOME_LOCATION_DATA_INTEND_ID)) {
            // Setting the location in `lastMarker.getPosition()` as home location
            (findViewById(R.id.submit_map_location)).setOnClickListener(v -> {
                Log.d("SubmitButtonMap", "HIT - HOME");
                File file = new File(getFilesDir() + USER_LOCATION_DATA);
                checkIfLocationFileExists();
                try {
                    List<String> lines = Files.readAllLines(file.toPath());
                    lines.set(0, HOME_LOCATION_FORMAT + EditUserDataActivity.DATA_NAME_VALUE_DELIMITER
                            + lastMarker.getPosition().latitude + EditUserDataActivity.DATA_NAME_VALUE_DELIMITER
                            + lastMarker.getPosition().longitude);
                    Log.d("MapActivity DEBUG LOCATION", "HOME - " + lines.get(1));
                    Files.write(file.toPath(), lines);
                    Toast.makeText(this, "Home location saved!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.e("OrganizationFragment - DATA LOCATION", e.toString());
                }
            });
            Log.d("OnNewIntent MapActivity", "Added listener to home");
        }
        if (Objects.equals(intent.getStringExtra(GET_LOCATION), WORK_LOCATION_DATA_INTENT_ID)) {
            // Setting the location in `lastMarker.getPosition()` as work location
            (findViewById(R.id.submit_map_location)).setOnClickListener(v -> {
                Log.d("SubmitButtonMap", "HIT - WORK");
                File file = new File(getFilesDir() + USER_LOCATION_DATA);
                checkIfLocationFileExists();
                try {
                    List<String> lines = Files.readAllLines(file.toPath());
                    lines.set(1, WORK_LOCATION_FORMAT + EditUserDataActivity.DATA_NAME_VALUE_DELIMITER
                            + lastMarker.getPosition().latitude + EditUserDataActivity.DATA_NAME_VALUE_DELIMITER
                            + lastMarker.getPosition().longitude);
                    Log.d("MapActivity DEBUG LOCATION", "WORK - " + lines.get(1));
                    Files.write(file.toPath(), lines);
                    Toast.makeText(this, "Work location saved!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.e("OrganizationFragment - DATA LOCATION", e.toString());
                }
            });
            Log.d("OnNewIntent MapActivity", "Added Listener to work");
        }
        Log.d("OnNewIntent MapActivity", "Intent was handled");
        super.onNewIntent(intent);
    }

    private void checkIfLocationFileExists() {
        File file = new File(getFilesDir() + USER_LOCATION_DATA);
        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                if (success) {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                    String baseText = HOME_LOCATION_FORMAT + EditUserDataActivity.DATA_NAME_VALUE_DELIMITER + "\n"
                            + WORK_LOCATION_FORMAT + EditUserDataActivity.DATA_NAME_VALUE_DELIMITER;
                    bw.write(baseText);
                    bw.close();
                }
            } catch (IOException e) {
                Log.e("OrganizationFragment - DATA LOCATION", e.toString());
            }
        }
    }
}
