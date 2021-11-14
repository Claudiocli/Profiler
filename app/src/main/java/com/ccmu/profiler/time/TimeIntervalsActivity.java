package com.ccmu.profiler.time;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ccmu.profiler.MainActivity;
import com.ccmu.profiler.R;

import java.util.ArrayList;
import java.util.Calendar;

public class TimeIntervalsActivity extends Activity {
    private static final int REQUEST_NOTIFICATION_PERMISSIONS_CODE = 26;

    private static final String SHARED_PREFERENCE_INTERVAL_START_PREFIX = "time_interval_start_num_";
    private static final String SHARED_PREFERENCE_INTERVAL_END_PREFIX = "time_interval_end_num_";
    private static final String SHARED_PREFERENCE_STATUS_INTERVAL_PREFIX = "status_time_interval_num_";
    private static final String SHARED_PREFERENCE_INTERVALS_LIST_SIZE = "time_intervals_size";

    private ListView timeIntervalsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_intervals_settings);

        timeIntervalsList = findViewById(R.id.time_intervals);

        registerForContextMenu(timeIntervalsList);

        if (!((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult(intent, REQUEST_NOTIFICATION_PERMISSIONS_CODE);
        }
    }

    public void deleteTimeIterval(View view) {
        int position = timeIntervalsList.getPositionForView((View) view.getParent().getParent().getParent());
        TimeIntervalAdapter adapter = ((TimeIntervalAdapter) timeIntervalsList.getAdapter());

        adapter.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        repopulateTimeIntervalsList();
        Log.d(getClass().getSimpleName(), "List repopulated");
        super.onResume();
    }

    private void repopulateTimeIntervalsList() {
        TimeIntervalAdapter adapter = (TimeIntervalAdapter) timeIntervalsList.getAdapter();

        if (adapter == null) {
            ArrayList<TimeIntervalModel> timeIntervals = new ArrayList<>();
            adapter = new TimeIntervalAdapter(this, timeIntervals);
            timeIntervalsList.setAdapter(adapter);
        }
        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE);
        int ub = sp.getInt(SHARED_PREFERENCE_INTERVALS_LIST_SIZE, 0);

        for (int i = 0; i < ub; i++) {
            String start = sp.getString(SHARED_PREFERENCE_INTERVAL_START_PREFIX + i, null);
            String end = sp.getString(SHARED_PREFERENCE_INTERVAL_END_PREFIX + i, null);

            if (start == null || end == null)
                continue;

            TimeIntervalModel tim = new TimeIntervalModel(start, end);

            adapter.add(tim);
        }
        adapter.notifyDataSetChanged();

    }

    public void addInterval(View view) {
        TimeIntervalAdapter adapter = (TimeIntervalAdapter) timeIntervalsList.getAdapter();

        if (adapter == null) {
            ArrayList<TimeIntervalModel> timeIntervals = new ArrayList<>();
            adapter = new TimeIntervalAdapter(view.getContext(), timeIntervals);
            timeIntervalsList.setAdapter(adapter);
        }

        String start_time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
        String end_time = (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1) + ":" + Calendar.getInstance().get(Calendar.MINUTE);

        adapter.add(new TimeIntervalModel(start_time, end_time));

        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(SHARED_PREFERENCE_INTERVAL_START_PREFIX + (adapter.getList().size() - 1), start_time);
        editor.putString(SHARED_PREFERENCE_INTERVAL_END_PREFIX + (adapter.getList().size() - 1), end_time);
        editor.putInt(SHARED_PREFERENCE_INTERVALS_LIST_SIZE, adapter.getList().size() - 1);
        editor.apply();

        adapter.notifyDataSetChanged();
    }

    public void addIntervalStart(View view) {
        Dialog timeDialog = new TimePickerDialog(
                view.getContext(),
                (timePicker, hour, minute) -> {
                    String value = hour + ":" + minute;

                    ListView listView = findViewById(R.id.time_intervals);
                    int index = listView.getPositionForView(view);
                    TimeIntervalModel timeInterval = (TimeIntervalModel) listView.getItemAtPosition(index);

                    timeInterval.changeInterval(value);

                    TextView tc = (TextView) view;
                    tc.setText(value);

                    SharedPreferences.Editor editor = view.getContext().getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE).edit();
                    editor.putString(SHARED_PREFERENCE_INTERVAL_START_PREFIX + index, value);
                    editor.apply();
                },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE),
                true);
        timeDialog.show();
    }

    public void addIntervalEnd(View view) {
        Dialog timeDialog = new TimePickerDialog(
                view.getContext(),
                (timePicker, hour, minute) -> {
                    String value = hour + ":" + minute;

                    ListView listView = findViewById(R.id.time_intervals);
                    int index = listView.getPositionForView(view);
                    TimeIntervalModel timeInterval = (TimeIntervalModel) listView.getItemAtPosition(index);

                    timeInterval.changeInterval(value);

                    TextView tc = (TextView) view;
                    tc.setText(value);

                    SharedPreferences.Editor editor = view.getContext().getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE).edit();
                    editor.putString(SHARED_PREFERENCE_INTERVAL_END_PREFIX + index, value);
                    editor.apply();
                },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE),
                true);
        timeDialog.show();
    }

    public void switchStatusTimeInterval(View view) {
        boolean value = false;

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchButton = (Switch) view;

        if (switchButton.isChecked())
            value = true;

        int index = ((ListView) findViewById(R.id.time_intervals)).getPositionForView((View) (switchButton.getParent().getParent().getParent()));
        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(SHARED_PREFERENCE_STATUS_INTERVAL_PREFIX + index, value);
        editor.apply();

        doSetStatusAlarm(switchButton.isChecked(), index);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void doSetStatusAlarm(boolean b, int index) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        String start = getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE).getString(SHARED_PREFERENCE_INTERVAL_START_PREFIX + index, null);
        String end = getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE).getString(SHARED_PREFERENCE_INTERVAL_END_PREFIX + index, null);

        if (start == null || end == null) throw new IllegalStateException();

        String[] start_split = start.split(":");
        String[] end_split = end.split(":");
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_split[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(start_split[1]));
        calendar.set(Calendar.SECOND, 0);
        long startingTime = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(end_split[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(end_split[1]));
        calendar.set(Calendar.SECOND, 0);
        long endingTime = calendar.getTimeInMillis();

        // TODO: create the service DNDService to handle how to enable and disable DoNotDisturb mode
        Intent intent = new Intent(this, DNDService.class);
        intent.putExtra(DNDService.STATUS_EXTRA, b);

        if (b) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    startingTime,
                    endingTime,
                    PendingIntent.getService(
                            this,
                            DNDService.REQUEST_CODE,
                            intent,
                            PendingIntent.FLAG_ONE_SHOT
                    )
            );
        } else {
            alarmManager.cancel(PendingIntent.getService(
                    this,
                    DNDService.REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
                    )
            );
        }

        Log.d("TimeIntervalsActivity", "changed status of the Alarm - " + (b ? "on" : "off"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NOTIFICATION_PERMISSIONS_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, R.string.notification_permissions_needed, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
