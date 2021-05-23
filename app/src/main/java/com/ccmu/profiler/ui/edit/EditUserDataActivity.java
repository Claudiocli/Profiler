package com.ccmu.profiler.ui.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ccmu.profiler.MainActivity;
import com.ccmu.profiler.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public class EditUserDataActivity extends Activity {

    public static final String DATA_NAME_VALUE_DELIMITER = "~";

    public static final String EDIT_MODE_INTENT = "edit_mode_intent";
    public static final String REGISTER_USER_MODE = "register_user_mode";
    public static final String EDIT_USER_MODE = "edit_user_mode";

    public static final int REGISTER_USER_REQUEST_CODE = 5;
    private static final int PICK_IMAGE_REQUEST_CODE = 6;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_user_data_activity);

        if (getIntent() != null && Objects.equals(getIntent().getStringExtra(EDIT_MODE_INTENT), REGISTER_USER_MODE))
            displayRegisterInfo();

        if (!(new File(getFilesDir() + MainActivity.USER_DATA_FILE_NAME)).exists())
            createUserDataFile();

        setObserverToDataViews();
    }

    private void createUserDataFile() {
        try {
            File file = new File(getFilesDir() + MainActivity.USER_DATA_FILE_NAME);
            boolean successfullyCreated = file.createNewFile();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
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
            String setup = R.string.name_format + DATA_NAME_VALUE_DELIMITER + "\n" +
                    R.string.surname_format + DATA_NAME_VALUE_DELIMITER + "\n" +
                    R.string.bio_format + DATA_NAME_VALUE_DELIMITER + "\n" +
                    R.string.linkedin_comparison + DATA_NAME_VALUE_DELIMITER + "\n" +
                    R.string.website_comparison + DATA_NAME_VALUE_DELIMITER + "\n" +
                    R.string.google_comparison + DATA_NAME_VALUE_DELIMITER + "\n" +
                    R.string.facebook_comparison + DATA_NAME_VALUE_DELIMITER + "\n" +
                    R.string.whatsapp_comparison + DATA_NAME_VALUE_DELIMITER + "\n" +
                    R.string.telegram_comparison + DATA_NAME_VALUE_DELIMITER + "\n";
            bw.write(setup);

            if (!successfullyCreated)
                Log.d("EditUserData - FILE CREATION", "File already exists. It should not.");
        } catch (IOException e) {
            Log.e("EditUserData - FILE CREATION", e.toString());
        }
    }

    private void displayRegisterInfo() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.register_user_title)
                .setMessage(R.string.register_user_message)
                .setIcon(R.drawable.ic_register_user)
                .show();
    }

    @SuppressLint("IntentReset")
    private void setObserverToDataViews() {
        ((TextView) findViewById(R.id.first_name_edit)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                File file = new File(getFilesDir() + MainActivity.USER_DATA_FILE_NAME);
                try {
                    List<String> lines = Files.readAllLines(file.toPath());
                    lines.set(0, getResources().getString(R.string.name_format) + DATA_NAME_VALUE_DELIMITER + s.toString());
                } catch (IOException e) {
                    Log.e("EditUserData - EDIT FIRST NAME VALUE", "data file should exists. Unknown I/O error");
                }
            }
        });
        ((TextView) findViewById(R.id.last_name_edit)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                File file = new File(getFilesDir() + MainActivity.USER_DATA_FILE_NAME);
                try {
                    List<String> lines = Files.readAllLines(file.toPath());
                    lines.set(1, getResources().getString(R.string.surname_format) + DATA_NAME_VALUE_DELIMITER + s.toString());
                } catch (IOException e) {
                    Log.e("EditUserData - EDIT LAST NAME VALUE", "data file should exists. Unknown I/O error");
                }
            }
        });
        ((TextView) findViewById(R.id.bio_and_info_edit)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                File file = new File(getFilesDir() + MainActivity.USER_DATA_FILE_NAME);
                try {
                    List<String> lines = Files.readAllLines(file.toPath());
                    lines.set(2, getResources().getString(R.string.bio_format) + DATA_NAME_VALUE_DELIMITER + s.toString());
                } catch (IOException e) {
                    Log.e("EditUserData - EDIT BIO INFO VALUE", "data file should exists. Unknown I/O error");
                }
            }
        });

        findViewById(R.id.image_edit).setOnClickListener(v -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");
            Intent pickIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select an Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST_CODE);
        });

        // TODO: add an observer to every link
        int[] linkViewsIds = new int[]{
                R.id.linkedin_edit_link, R.id.google_edit_link,
                R.id.website_edit_link, R.id.facebook_link_edit,
                R.id.whatsapp_edit_link, R.id.telegram_edit_link
        };

        for (int id : linkViewsIds) {
            ((TextView) findViewById(id)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @SuppressLint("NonConstantResourceId")
                @Override
                public void afterTextChanged(Editable s) {
                    File file = new File(getFilesDir() + MainActivity.USER_DATA_FILE_NAME);
                    try {
                        List<String> lines = Files.readAllLines(file.toPath());
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
                        switch (id) {
                            case R.id.linkedin_edit_link:
                                findViewById(R.id.linkedinPersonalLink).setOnClickListener(v -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(s.toString()));
                                    startActivity(intent);
                                });
                                lines.set(3, getResources().getString(R.string.linkedin_comparison) + DATA_NAME_VALUE_DELIMITER + s.toString());
                                break;
                            case R.id.website_edit_link:
                                findViewById(R.id.websitePersonalLink).setOnClickListener(v -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(s.toString()));
                                    startActivity(intent);
                                });
                                lines.set(4, getResources().getString(R.string.website_comparison) + DATA_NAME_VALUE_DELIMITER + s.toString());
                                break;
                            case R.id.google_edit_link:
                                findViewById(R.id.googlePersonalLink).setOnClickListener(v -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(s.toString()));
                                    startActivity(intent);
                                });
                                lines.set(5, getResources().getString(R.string.google_comparison) + DATA_NAME_VALUE_DELIMITER + s.toString());
                                break;
                            case R.id.facebook_link_edit:
                                findViewById(R.id.facebookPersonalLink).setOnClickListener(v -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(s.toString()));
                                    startActivity(intent);
                                });
                                lines.set(6, getResources().getString(R.string.facebook_comparison) + DATA_NAME_VALUE_DELIMITER + s.toString());
                                break;
                            case R.id.whatsapp_edit_link:
                                findViewById(R.id.whatsappPersonalLink).setOnClickListener(v -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(s.toString()));
                                    startActivity(intent);
                                });
                                lines.set(7, getResources().getString(R.string.whatsapp_comparison) + DATA_NAME_VALUE_DELIMITER + s.toString());
                                break;
                            case R.id.telegram_edit_link:
                                findViewById(R.id.telegramPersonalLink).setOnClickListener(v -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(s.toString()));
                                    startActivity(intent);
                                });
                                lines.set(8, getResources().getString(R.string.telegram_comparison) + DATA_NAME_VALUE_DELIMITER + s.toString());
                                break;
                        }
                    } catch (IOException e) {
                        Log.e("EditUserData - EDIT LINK VALUES", "data file should exists. Unknown I/O error");
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            File file = new File(getFilesDir() + MainActivity.USER_DATA_IMAGE_FILE_NAME);
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();

                if ((options.outWidth == -1) || (options.outHeight == -1))
                    throw new FileNotFoundException("File corrupt or nonexistent");

                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                ((ImageView) findViewById(R.id.image_edit)).setImageBitmap(bitmap);
                ((ImageView) findViewById(R.id.profilePic)).setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("EditUserData - IMAGE PICK ACTIVITY RESULT", e.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
