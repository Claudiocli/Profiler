package com.ccmu.profiler.map;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ccmu.profiler.MainActivity;
import com.ccmu.profiler.R;
import com.google.android.gms.maps.model.LatLng;

public class MapService extends Service implements LocationListener {

    public static final String STATUS_SHARED_KEY = "map_service_status";

    public static final int REQUEST_CODE = 127;

    // Time between each check on the current position in millisecond - 10 min (10*60*1000)
    public static final long UPDATE_INTERVAL = 10 * 60 * 1000;
    public static final float MINIMUM_METERS_TO_UPDATE = 20f;
    // Radius from the work location in which the phone must be to enable the dnd
    private static final float DISTANCE_THRESHOLD = 50f;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(getClass().getSimpleName(), "Creating service");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(getClass().getSimpleName(), "Destroying service");

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        Log.d(this.getClass().getSimpleName(), "DNDAW Disabled");

        SharedPreferences sp = getBaseContext().getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.remove(STATUS_SHARED_KEY);
        editor.apply();

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE);
        boolean status = sp.getBoolean(STATUS_SHARED_KEY, false);

        if (status) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.location_permissions_error, Toast.LENGTH_LONG).show();
                // TODO: Redirect to Permissions page
            }
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            // Can use a FusedLocationManager?
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    UPDATE_INTERVAL,
                    MINIMUM_METERS_TO_UPDATE,
                    this
            );
            Log.d(getClass().getSimpleName(), "Request location update set");
        }
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        LatLng latLng = new LatLng(Float.parseFloat(sp.getString(MapActivity.WORK_LATITUDE_KEY, "0.0")), Float.parseFloat(sp.getString(MapActivity.WORK_LONGITUDE_KEY, "0.0")));
        Location workLocation = new Location("");

        workLocation.setLatitude(latLng.latitude);
        workLocation.setLongitude(latLng.longitude);

        // use Location.distanceTo(Location dest) :  https://developer.android.com/reference/android/location/Location.html#distanceTo%28android.location.Location%29
        if (nm.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_ALL) {
            // If DND not active
            if (workLocation.distanceTo(location) <= DISTANCE_THRESHOLD) {
                nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                Log.d(this.getClass().getSimpleName(), "DNDAW Enabled");
            }
        } else if (nm.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_NONE) {
            // If DND active
            if (workLocation.distanceTo(location) > DISTANCE_THRESHOLD) {
                nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                Log.d(this.getClass().getSimpleName(), "DNDAW Disabled");
            }
        }
        Log.d(getClass().getSimpleName(), "Location changed!");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        stopSelf();

        SharedPreferences sp = getBaseContext().getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.remove(STATUS_SHARED_KEY);
        editor.apply();
    }
}
