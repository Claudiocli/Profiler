package com.ccmu.profiler.ui.exchange;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ccmu.profiler.R;
import com.ccmu.profiler.ui.nfc.NFCActivity;

public class ExchangeFragment extends Fragment {

    private static final int REQUEST_HOME_LOCATION = 21;
    private static final int REQUEST_WORK_LOCATION = 22;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ExchangeViewModel dashboardViewModel = new ViewModelProvider(
                getViewModelStore(), getDefaultViewModelProviderFactory())
                .get(ExchangeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_exchange, container, false);

        setNFCExchangeListeners(root);

        return root;
    }

    private void setNFCExchangeListeners(View root) {
        root.findViewById(R.id.nfc_receive_contact).setOnClickListener(v -> {
            if (NFCActivity.isNFCDisabled())
                return;
            Intent i = new Intent(getContext(), NFCActivity.class);
            i.putExtra(NFCActivity.MODE, NFCActivity.NFC_READ_MODE);
            startActivity(i);
            Toast.makeText(root.getContext(), R.string.nfc_contact_received, Toast.LENGTH_LONG);
        });
        root.findViewById(R.id.nfc_send_my_contact).setOnClickListener(v -> {
            if (NFCActivity.isNFCDisabled())
                return;
            Intent i = new Intent(getContext(), NFCActivity.class);
            i.putExtra(NFCActivity.MODE, NFCActivity.NFC_WRITE_MODE);
            startActivity(i);
            Toast.makeText(root.getContext(), R.string.nfc_contact_sent, Toast.LENGTH_LONG);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

}