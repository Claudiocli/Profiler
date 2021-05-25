package com.ccmu.profiler.ui.organization;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ccmu.profiler.R;
import com.ccmu.profiler.map.MapActivity;

public class OrganizationFragment extends Fragment {

    private static final int REQUEST_HOME_LOCATION = 21;
    private static final int REQUEST_WORK_LOCATION = 22;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        OrganizationViewModel dashboardViewModel = new ViewModelProvider(
                getViewModelStore(), getDefaultViewModelProviderFactory())
                .get(OrganizationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_organization, container, false);
        // TODO: set a position to be your work position, to, maybe, set a specific notification options
        // Ask permission only if user want to enable it

        // TODO: let MapActivity handle the storage of location data
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.organization_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.set_home_position) {
            startActivity(new Intent(getContext(), MapActivity.class).putExtra(
                    MapActivity.GET_LOCATION, MapActivity.WORK_LOCATION_DATA_INTENT_ID));
        }
        if (item.getItemId() == R.id.set_work_position) {
            startActivity(new Intent(getContext(), MapActivity.class).putExtra(
                    MapActivity.GET_LOCATION, MapActivity.WORK_LOCATION_DATA_INTENT_ID));
        }
        return super.onOptionsItemSelected(item);
    }

}