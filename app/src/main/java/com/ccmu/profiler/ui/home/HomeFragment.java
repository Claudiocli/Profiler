package com.ccmu.profiler.ui.home;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ccmu.profiler.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        final TextView user_name=(TextView) root.findViewById(R.id.name);
        final TextView user_surname=(TextView) root.findViewById(R.id.surname);
        final TextView user_bio=(TextView) root.findViewById(R.id.bio);

        homeViewModel.getUserName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                user_name.setText(s);
            }
        });
        homeViewModel.getUserSurame().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                user_surname.setText(s);
            }
        })
        ;homeViewModel.getUserBio().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                user_bio.setText(s);
            }
        });


        TextView googleLinkTextView=(TextView) root.findViewById(R.id.googlePersonalLink);
        googleLinkTextView.setText(Html.fromHtml(getString(R.string.google_name), Html.FROM_HTML_MODE_LEGACY));

        return root;
    }
}