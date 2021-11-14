package com.ccmu.profiler.time;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ccmu.profiler.R;

import java.util.List;
import java.util.Objects;

public class TimeIntervalAdapter extends ArrayAdapter<TimeIntervalModel> {

    private final List<TimeIntervalModel> objects;

    public TimeIntervalAdapter(@NonNull Context context, @NonNull List<TimeIntervalModel> objects) {
        super(context, R.layout.item_time_interval, objects);
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TimeIntervalModel timeIntervalModel = Objects.requireNonNull(getItem(position));

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_time_interval, parent, false);

        TextView startTime = convertView.findViewById(R.id.start_interval);
        TextView endTime = convertView.findViewById(R.id.end_interval);

        startTime.setText(timeIntervalModel.startTimeInterval());
        endTime.setText(timeIntervalModel.endTimeInterval());

        return convertView;
    }

    public List<TimeIntervalModel> getList() {
        return this.objects;
    }

    public void add(TimeIntervalModel t) {
        this.objects.add(t);
    }

    public void remove(int index) {
        this.objects.remove(index);
    }

    public int getIndexOfItem(TimeIntervalModel t) {
        return this.objects.indexOf(t);
    }
}
