package com.ccmu.profiler.ui.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.ccmu.profiler.R;

public class ContactSettings extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contacts_settings_ui);
    }

    public void sortContacts(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sort_by)
                .setSingleChoiceItems(new CharSequence[]{"First name", "Last name"},
                        -1,
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    ContactsFragment.changeDefaultOrder(ContactsFragment.ORDER_BY_FIRST_NAME);
                                    dialog.dismiss();
                                    onBackPressed();
                                    break;
                                case 1:
                                    ContactsFragment.changeDefaultOrder(ContactsFragment.ORDER_BY_LAST_NAME);
                                    dialog.dismiss();
                                    onBackPressed();
                                    break;
                            }
                        })
                .show();
    }
}
