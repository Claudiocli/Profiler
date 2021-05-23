package com.ccmu.profiler.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.ccmu.profiler.ui.edit.EditUserDataActivity;
import com.ccmu.profiler.ui.nfc.NFCActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(getViewModelStore(), getDefaultViewModelProviderFactory()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        addObserverForChanges(homeViewModel, root);
        setNFCExchangeListeners(root);

        TextView googleLinkTextView = root.findViewById(R.id.googlePersonalLink);
        googleLinkTextView.setText(Html.fromHtml(getString(R.string.google_name), Html.FROM_HTML_MODE_LEGACY));

        checkAndUpdateUserData(root);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.home_settings) {
            // TODO: startActivity to settings
            // startActivity(new Intent(getContext(), HomeSettings.class));
            return true;
        }
        if (item.getItemId() == R.id.home_profile) {
            startActivity(new Intent(getContext(), EditUserDataActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAndUpdateUserData(View root) {
        File file = new File(requireContext().getFilesDir() + MainActivity.USER_DATA_FILE_NAME);
        if (!file.exists())
            return;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ArrayList<String> userData = new ArrayList<>();
            String currLine = reader.readLine();
            while (currLine != null) {
                userData.add(currLine);
                currLine = reader.readLine();
            }
            if (userData.size() == 0)
                return;
            /*
                Format:
                NAME~Ciccio
                SURNAME~Pasticcio
                BIO~Born in xxxx, Ciccio, is a ...
                LINKEDIN~https://
                WEBSITE~https://
                GOOGLE~https://
                FACEBOOK~https://
                WHATSAPP~https://
                TELEGRAM~https://
                .
                .
                .
                OTHER~
            */
            String user_first_name = userData.get(0).substring(userData.get(0).indexOf(EditUserDataActivity.DATA_NAME_VALUE_DELIMITER) + 1);
            String user_last_name = userData.get(1).substring(userData.get(1).indexOf(EditUserDataActivity.DATA_NAME_VALUE_DELIMITER) + 1);
            String user_bio_and_info = userData.get(2).substring(userData.get(2).indexOf(EditUserDataActivity.DATA_NAME_VALUE_DELIMITER) + 1);
            userData.remove(0);
            userData.remove(1);
            userData.remove(2);
            // Now there are only links
            for (String s : userData) {
                String linkTo = s.substring(0, s.indexOf(EditUserDataActivity.DATA_NAME_VALUE_DELIMITER));
                String link = s.substring(s.indexOf(EditUserDataActivity.DATA_NAME_VALUE_DELIMITER) + 1);

                if (!link.startsWith("http://") && !link.startsWith("https://"))
                    link = "http://" + link;

                if (linkTo.equals(root.getResources().getString(R.string.linkedin_comparison))) {
                    String finalLinkedinLink = link;
                    root.findViewById(R.id.linkedinPersonalLink).setOnClickListener(
                            v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalLinkedinLink))));
                } else if (linkTo.equals(root.getResources().getString(R.string.website_comparison))) {
                    String finalWebsiteLink = link;
                    root.findViewById(R.id.websitePersonalLink).setOnClickListener(
                            v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalWebsiteLink))));
                } else if (linkTo.equals(root.getResources().getString(R.string.google_comparison))) {
                    String finalGoogleLink = link;
                    root.findViewById(R.id.googlePersonalLink).setOnClickListener(
                            v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalGoogleLink))));
                } else if (linkTo.equals(root.getResources().getString(R.string.facebook_comparison))) {
                    String finalFacebookLink = link;
                    root.findViewById(R.id.facebookPersonalLink).setOnClickListener(
                            v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalFacebookLink))));
                } else if (linkTo.equals(root.getResources().getString(R.string.whatsapp_comparison))) {
                    String finalWhatsappLink = link;
                    root.findViewById(R.id.whatsappPersonalLink).setOnClickListener(
                            v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalWhatsappLink))));
                } else if (linkTo.equals(root.getResources().getString(R.string.telegram_comparison))) {
                    String finalTelegramLink = link;
                    root.findViewById(R.id.telegramPersonalLink).setOnClickListener(
                            v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalTelegramLink))));
                }
            }

            ((TextView) root.findViewById(R.id.name)).setText(user_first_name);
            ((TextView) root.findViewById(R.id.surname)).setText(user_last_name);
            ((TextView) root.findViewById(R.id.bio)).setText(user_bio_and_info);

            File image = new File(requireContext().getFilesDir() + MainActivity.USER_DATA_IMAGE_FILE_NAME);
            if (image.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());
                ((ImageView) root.findViewById(R.id.profilePic)).setImageBitmap(bitmap);
            }

        } catch (FileNotFoundException e) {
            Log.e("USER_DATA_FILE - FileNotFoundException", e.toString());
        } catch (IOException e) {
            Log.e("USER_DATA_FILE - IOException", e.toString());
        }
    }

    private void addObserverForChanges(HomeViewModel homeViewModel, View root) {

        final TextView user_name = root.findViewById(R.id.name);
        final TextView user_surname = root.findViewById(R.id.surname);
        final TextView user_bio = root.findViewById(R.id.bio);

        homeViewModel.getUserName().observe(getViewLifecycleOwner(), s -> {
            if (!s.equals(""))
                user_name.setText(s);
        });
        homeViewModel.getUserSurname().observe(getViewLifecycleOwner(), s -> {
            if (!s.equals(""))
                user_surname.setText(s);
        })
        ;
        homeViewModel.getUserBio().observe(getViewLifecycleOwner(), s -> {
            if (!s.equals(""))
                user_bio.setText(s);
        });
    }

    private void setNFCExchangeListeners(View root) {
        root.findViewById(R.id.nfc_receive_contact).setOnClickListener(v -> {
            if (NFCActivity.isNFCDisabled())
                return;
            Intent i = new Intent(getContext(), NFCActivity.class);
            i.putExtra(NFCActivity.ID_INTENT_DATA, NFCActivity.NFC_READ_MODE);
            startActivity(i);
        });
        root.findViewById(R.id.nfc_send_my_contact).setOnClickListener(v -> {
            if (NFCActivity.isNFCDisabled())
                return;
            Intent i = new Intent(getContext(), NFCActivity.class);
            i.putExtra(NFCActivity.ID_INTENT_DATA, NFCActivity.NFC_WRITE_MODE);
            startActivity(i);
        });
    }

}