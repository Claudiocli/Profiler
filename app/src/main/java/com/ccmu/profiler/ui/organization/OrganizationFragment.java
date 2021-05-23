package com.ccmu.profiler.ui.organization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ccmu.profiler.R;

public class OrganizationFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        OrganizationViewModel dashboardViewModel = new ViewModelProvider(
                getViewModelStore(), getDefaultViewModelProviderFactory())
                .get(OrganizationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_organization, container, false);
        // TODO: set a position to be your work position, to maybe set a specific notification options
        // Ask permission only if user want to enable it
        return root;
    }
}