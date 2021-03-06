package com.ccmu.profiler.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.ccmu.profiler.MainActivity;
import com.ccmu.profiler.R;
import com.ccmu.profiler.map.MapActivity;
import com.ccmu.profiler.map.MapService;

import java.util.Calendar;

public class HomeSettings extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_settings_ui);

        checkButtonStatus();
    }

    private void checkButtonStatus() {
        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE);
        Button button = findViewById(R.id.dndmaw_button);

        if (sp.getBoolean(MapService.STATUS_SHARED_KEY, false)) {
            button.setBackgroundColor(Color.GREEN);
        } else {
            button.setBackgroundColor(Color.RED);
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void changeStatusMapService(View view) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MapActivity.LOCATION_REQUEST_CODE);

        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        boolean currentStatus = sp.getBoolean(MapService.STATUS_SHARED_KEY, false);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(view.getContext(), MapService.class);

        if (!currentStatus) {
            alarmManager.setRepeating(
                    AlarmManager.RTC,
                    Calendar.getInstance().getTimeInMillis(),
                    MapService.UPDATE_INTERVAL,
                    PendingIntent.getBroadcast(
                            view.getContext(),
                            MapService.REQUEST_CODE,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    )
            );
            startService(intent);
            view.setBackgroundColor(Color.GREEN);
            Log.d(getClass().getSimpleName(), "started repeating action");
        } else {
            alarmManager.cancel(PendingIntent.getBroadcast(
                    view.getContext(),
                    MapService.REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                    )
            );
            stopService(intent);
            view.setBackgroundColor(Color.RED);
            Log.d(getClass().getSimpleName(), "Canceled repeating action");
        }
        Log.d(getClass().getSimpleName(), "Button pressed");
        editor.putBoolean(MapService.STATUS_SHARED_KEY, !currentStatus);
        editor.apply();
    }
}
