package com.ccmu.profiler.time;

import androidx.annotation.NonNull;

public class TimeIntervalModel {

    private String startTimeInterval;
    private String endTimeInterval;
    private boolean activated;

    public TimeIntervalModel(@NonNull String startTimeInterval, @NonNull String endTimeInterval) {
        this.startTimeInterval = startTimeInterval;
        this.endTimeInterval = endTimeInterval;
        this.activated = false;
    }

    public String startTimeInterval() {
        return startTimeInterval;
    }

    public String endTimeInterval() {
        return endTimeInterval;
    }

    public void switchStatus() {
        this.activated = !this.activated;
    }

    public void changeInterval(String interval) {
        String[] split = interval.split(":");
        startTimeInterval = split[0];
        endTimeInterval = split[1];
    }

}
