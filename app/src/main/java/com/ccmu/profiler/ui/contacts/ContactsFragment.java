package com.ccmu.profiler.ui.contacts;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ccmu.profiler.R;
import com.ccmu.profiler.ui.home.HomeFragment;

import java.util.ArrayList;

import static android.Manifest.permission.READ_CONTACTS;

public class ContactsFragment extends Fragment    {

    private static final int REQUEST_CODE_CONTACTS_READ=1;

    private ListView contactsList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);

        contactsList = (ListView) root.findViewById(R.id.contactsList);

        if (requireContext().checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_DENIED)
            requireActivity().requestPermissions(new String[]{READ_CONTACTS}, REQUEST_CODE_CONTACTS_READ);
        else
            doShowContacts();

        return root;
    }

    private void doShowContacts() {
        ContentResolver cr = requireContext().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER},
                null,
                null,
                null);

        ArrayList<Contact> contacts = new ArrayList<>();
        ContactAdapter contactsAdapter= new ContactAdapter(requireContext(), contacts);

        contactsList.setAdapter(contactsAdapter);

        if ((cursor != null ? cursor.getCount() : 0) > 0) {
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String name = cursor.getString(cursor.getColumnIndex((ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                ArrayList<String> numberArray = new ArrayList<>();
                if (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER) > 0)  {
                    Cursor cursor1 = requireContext().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",
                        new String[]{contactId},
                        null);
                    if (cursor1 != null)
                        while (cursor1.moveToNext())
                            numberArray.add(cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    if (cursor1 != null)
                        cursor1.close();
                }
                contactsAdapter.add(new Contact(contactId, name, numberArray.toArray(new String[0])));
            }
        }

        if (cursor != null)
            cursor.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("CallBackPermissionResult", "I've tried so hard and got so far");
        if (requestCode == REQUEST_CODE_CONTACTS_READ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doShowContacts();
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_alert_no_permission)
                        .setMessage(R.string.message_alert_no_permission)
                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.navigation_home, new HomeFragment())
                                        .commit();
                            }
                        })
                        .setIcon(R.drawable.ic_error)
                        .show();
            }
        }
    }

}