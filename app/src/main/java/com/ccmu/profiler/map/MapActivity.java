package com.ccmu.profiler.map;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.ccmu.profiler.MainActivity;
import com.ccmu.profiler.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

        Log.d("OnCreate MapActivity", "initializing supportMapFragment");
        SupportMapFragment supportMapFragment = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment));
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }
        Log.d("SupportMapFragment STATUS", (supportMapFragment == null) ? "null" : "instantiated");

        setupButton();

    }

    private void setupButton() {
        // Listener to save the last location of `lastMarker`
        (findViewById(R.id.submit_map_location)).setOnClickListener(v -> {
            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE).edit();

            editor.putString("Work_location_latitude", String.valueOf(lastMarker.getPosition().latitude));
            editor.putString("Work_location_longitude", String.valueOf(lastMarker.getPosition().longitude));
            editor.apply();

            Toast.makeText(this, "Work location saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
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
}
