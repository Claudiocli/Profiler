package com.ccmu.profiler.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ccmu.profiler.MainActivity;
import com.ccmu.profiler.R;
import com.ccmu.profiler.map.MapActivity;
import com.ccmu.profiler.time.TimeIntervalsActivity;
import com.ccmu.profiler.ui.edit.EditUserDataActivity;

public class HomeFragment extends Fragment {

    public static final String SET_URI_PROFILE_IMAGE_ID = "set_bitmap_id";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(getViewModelStore(),
                getDefaultViewModelProviderFactory()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        TextView googleLinkTextView = root.findViewById(R.id.googlePersonalLink);
        googleLinkTextView.setText(Html.fromHtml(getString(R.string.google_name), Html.FROM_HTML_MODE_LEGACY));

        updateUserData(root);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.set_work_position) {
            startActivity(new Intent(getContext(), MapActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.set_time_intervals) {
            startActivity(new Intent(getContext(), TimeIntervalsActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.home_profile) {
            startActivity(new Intent(getContext(), EditUserDataActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.home_settings) {
            startActivity(new Intent(getContext(), HomeSettings.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUserData(View root) {
        SharedPreferences sp = getContext().getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE);

        root.findViewById(R.id.linkedinPersonalLink).setOnClickListener(view -> {
            String s = sp.getString("Linkedin_link", "Linkedin");
            if (s == "")
                return;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(s));
            startActivity(intent);
        });
        root.findViewById(R.id.websitePersonalLink).setOnClickListener(view -> {
            String s = sp.getString("Website_link", "Website");
            if (s == "")
                return;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(s));
            startActivity(intent);
        });
        root.findViewById(R.id.googlePersonalLink).setOnClickListener(view -> {
            String s = sp.getString("Google_link", "Google");
            if (s == "")
                return;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(s));
            startActivity(intent);
        });
        root.findViewById(R.id.facebookPersonalLink).setOnClickListener(view -> {
            String s = sp.getString("Facebook_link", "Facebook");
            if (s == "")
                return;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(s));
            startActivity(intent);
        });
        root.findViewById(R.id.whatsappPersonalLink).setOnClickListener(view -> {
            String s = sp.getString("Whatsapp_link", "Whatsapp");
            if (s == "")
                return;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(s));
            startActivity(intent);
        });
        root.findViewById(R.id.telegramPersonalLink).setOnClickListener(view -> {
            String s = sp.getString("Telegram_link", "Telegram");
            if (s == "")
                return;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(s));
            startActivity(intent);
        });

        ((TextView) root.findViewById(R.id.name)).setText(getContext().getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE).getString("First_name", "First name"));
        ((TextView) root.findViewById(R.id.surname)).setText(getContext().getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE).getString("Last_name", "Last name"));
        ((TextView) root.findViewById(R.id.bio)).setText(getContext().getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE).getString("Bio", "Bio"));

        Log.d("UPDATE INFO USER", "name edited correctly");
        Log.d("UPDATE INFO USER", "surname edited correctly");
        Log.d("UPDATE INFO USER", "bio edited correctly");

        String uri = getContext().getSharedPreferences(MainActivity.SHARED_PROPERTY_KEY, Context.MODE_PRIVATE).getString("Uri_profile_pic", "");
        if (uri != "")
            ((ImageView) root.findViewById(R.id.profile_picture)).setImageURI(Uri.parse(uri));
    }


}