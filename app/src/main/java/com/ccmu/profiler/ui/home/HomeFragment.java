package com.ccmu.profiler.ui.home;

import android.content.Intent;
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
import java.nio.file.Files;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final String SET_URI_PROFILE_IMAGE_ID = "set_bitmap_id";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(getViewModelStore(),
                getDefaultViewModelProviderFactory()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

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
            List<String> userData = Files.readAllLines(file.toPath());
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
                if (!(!link.equals("") && !link.equals("http://")))
                    continue;

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

            Log.d("UPDATE INFO USER", "name edited into " + user_first_name);
            Log.d("UPDATE INFO USER", "surname edited into " + user_last_name);
            Log.d("UPDATE INFO USER", "bio edited into " + user_bio_and_info);

            File image = new File(requireContext().getFilesDir() + MainActivity.USER_DATA_IMAGE_FILE_NAME);
            if (image.exists()) {
                List<String> imageLines = Files.readAllLines(image.toPath());
                boolean noError = true;
                if (imageLines.size() == 0)
                    noError = !image.delete();
                else if (imageLines.get(0).length() == 0)
                    noError = !image.delete();
                else if (noError) {
                    Uri uri = Uri.parse(imageLines.get(0));
//                    ((ImageView) root.findViewById(R.id.profile_picture)).setImageURI(uri);
                }
            }

            reader.close();

        } catch (FileNotFoundException e) {
            Log.e("USER_DATA_FILE - FileNotFoundException", e.toString());
        } catch (IOException e) {
            Log.e("USER_DATA_FILE - IOException", e.toString());
        }
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