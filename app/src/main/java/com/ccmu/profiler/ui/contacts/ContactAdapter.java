package com.ccmu.profiler.ui.contacts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ccmu.profiler.R;

import java.util.List;
import java.util.Objects;

public class ContactAdapter extends ArrayAdapter<ContactModel> {

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     */
    public ContactAdapter(@NonNull Context context, List<ContactModel> resource) {
        super(context, 0, resource);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ContactModel contactModel = Objects.requireNonNull(getItem(position));

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);

        LinearLayout layout = convertView.findViewById(R.id.contact_info_layout);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.height = (displayMetrics.heightPixels) / 16;
        layout.setLayoutParams(params);

        TextView contactName = convertView.findViewById(R.id.contactName);
        TextView contactNumber = convertView.findViewById(R.id.contactNumber);

        contactName.setText("Name: " + contactModel.getFullName());
        String[] numbers = contactModel.getNumbers();
        contactNumber.setText("Number: " + numbers[0]);

        return convertView;
    }
}
