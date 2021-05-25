package com.ccmu.profiler;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ccmu.profiler.ui.edit.EditUserDataActivity;
import com.ccmu.profiler.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final String USER_DATA_FILE_NAME = "user.txt";
    public static final String USER_DATA_IMAGE_FILE_NAME = "user_image.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!doesUserDataExists())
            registerUser();

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_organization, R.id.navigation_contacts)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    private boolean doesUserDataExists() {
        return new File(getFilesDir().getPath() + USER_DATA_FILE_NAME).exists();
    }

    private void registerUser() {
        startActivityForResult(new Intent(this, EditUserDataActivity.class)
                        .putExtra(EditUserDataActivity.EDIT_MODE_INTENT, EditUserDataActivity.REGISTER_USER_MODE),
                EditUserDataActivity.REGISTER_USER_REQUEST_CODE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String uri = intent.getStringExtra(HomeFragment.SET_URI_PROFILE_IMAGE_ID);
        if (uri != null) {
            ((ImageView) findViewById(R.id.profile_picture)).setImageURI(Uri.parse(uri));
            Log.d("MainActivity - OnNewIntent", "SWAP IMAGE COMPLETED");
        }
        super.onNewIntent(intent);
    }
}