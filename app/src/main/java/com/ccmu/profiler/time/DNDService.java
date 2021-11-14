package com.ccmu.profiler.time;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class DNDService extends Service {

    public static final int REQUEST_CODE = 0;
    public static final String STATUS_EXTRA = "status";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Log.d("TimeIntervalsActivity", "Changed dnd status");

        if (intent.getBooleanExtra(STATUS_EXTRA, false)) {
            // Activating DND
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
            Log.d("TimeIntervalsActivity", "DND: ON");
        } else {
            // Deactivating DND
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            Log.d("TimeIntervalsActivity", "DND: OFF");
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
