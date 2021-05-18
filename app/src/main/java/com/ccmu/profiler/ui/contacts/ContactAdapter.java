package com.ccmu.profiler.ui.contacts;

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

import java.util.ArrayList;
import java.util.Objects;

public class ContactAdapter extends ArrayAdapter<Contact> {

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     */
    public ContactAdapter(@NonNull Context context, ArrayList<Contact> resource) {
        super(context, 0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Contact contact = Objects.requireNonNull(getItem(position));

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);

        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.contact_info_layout);
        ViewGroup.LayoutParams params= layout.getLayoutParams();
        DisplayMetrics displayMetrics=new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.height=(displayMetrics.heightPixels)/16;
        layout.setLayoutParams(params);

        TextView contactName = (TextView) convertView.findViewById(R.id.contactName);
        TextView contactNumber = (TextView) convertView.findViewById(R.id.contactNumber);

        contactName.append(": "+contact.getName());
        String[] numbers=contact.getNumbers();
        contactNumber.append(": ");
        if (numbers.length > 1)
            for (String s : numbers)
                contactNumber.append(s+" - ");
        else
            contactNumber.append(numbers[0]);

        return convertView;
    }
}
