package com.ccmu.profiler.ui.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.ccmu.profiler.R;

public class AddContactActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_contact_activity);

        Spinner mail_type = findViewById(R.id.add_contact_mail_spinner);

        mail_type.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Work", "Mobile", "Other"}));
    }

    public void addContact(View view) {
        EditText first_name = findViewById(R.id.add_contact_first_name);
        EditText last_name = findViewById(R.id.add_contact_last_name);
        EditText number = findViewById(R.id.add_contact_number);
        EditText mail = findViewById(R.id.add_contact_mail);


        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, first_name.getText() + " " + last_name.getText())
                .putExtra(ContactsContract.Intents.Insert.PHONE, number.getText())
                .putExtra(ContactsContract.Intents.Insert.EMAIL, mail.getText());

        Object choice = ((Spinner) findViewById(R.id.add_contact_mail_spinner)).getSelectedItem();
        if (choice.equals("Work"))
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        if (choice.equals("Mobile"))
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_MOBILE);
        if (choice.equals("Other"))
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER);

        startActivity(intent);
        finish();
    }

    public void cancel(View view) {
        finish();
    }

}
