package com.ccmu.profiler.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.ccmu.profiler.MainActivity;
import com.ccmu.profiler.R;
import com.ccmu.profiler.map.MapService;

import java.util.Calendar;

public class HomeSettings extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_settings_ui);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void changeStatusMapService(View view) {
        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        boolean currentStatus = sp.getBoolean(MapService.STATUS_SHARED_KEY, false);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(view.getContext(), MapService.class);

        if (!currentStatus) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    Calendar.getInstance().getTimeInMillis(),
                    MapService.UPDATE_INTERVAL,
                    PendingIntent.getService(
                            view.getContext(),
                            0,
                            intent,
                            PendingIntent.FLAG_ONE_SHOT)
            );
            view.setBackgroundColor(Color.GREEN);
        } else {
            alarmManager.cancel(PendingIntent.getService(
                    view.getContext(),
                    0,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT));
            view.setBackgroundColor(Color.RED);
        }

        editor.putBoolean(MapService.STATUS_SHARED_KEY, !currentStatus);
    }
}
