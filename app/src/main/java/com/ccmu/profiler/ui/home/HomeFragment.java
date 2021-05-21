package com.ccmu.profiler.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ccmu.profiler.R;
import com.ccmu.profiler.ui.nfc.NFCActivity;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(getViewModelStore(), getDefaultViewModelProviderFactory()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        addObserverForChanges(homeViewModel, root);
        setNFCExchangeListeners(root);

        TextView googleLinkTextView = root.findViewById(R.id.googlePersonalLink);
        googleLinkTextView.setText(Html.fromHtml(getString(R.string.google_name), Html.FROM_HTML_MODE_LEGACY));

        return root;
    }

    private void addObserverForChanges(HomeViewModel homeViewModel, View root) {

        final TextView user_name = root.findViewById(R.id.name);
        final TextView user_surname = root.findViewById(R.id.surname);
        final TextView user_bio = root.findViewById(R.id.bio);

        homeViewModel.getUserName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!s.equals(""))
                    user_name.setText(s);
            }
        });
        homeViewModel.getUserSurname().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!s.equals(""))
                    user_surname.setText(s);
            }
        })
        ;
        homeViewModel.getUserBio().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!s.equals(""))
                    user_bio.setText(s);
            }
        });
    }

    private void setNFCExchangeListeners(View root) {
        ((Button) root.findViewById(R.id.nfc_receive_contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NFCActivity.isNFCDisabled())
                    return;
                Intent i = new Intent(getContext(), NFCActivity.class);
                i.putExtra(NFCActivity.ID_INTENT_DATA, NFCActivity.NFC_READ_MODE);
                startActivity(i);
            }
        });
        ((Button) root.findViewById(R.id.nfc_send_my_contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NFCActivity.isNFCDisabled())
                    return;
                Intent i = new Intent(getContext(), NFCActivity.class);
                i.putExtra(NFCActivity.ID_INTENT_DATA, NFCActivity.NFC_WRITE_MODE);
                startActivity(i);
            }
        });
    }

}