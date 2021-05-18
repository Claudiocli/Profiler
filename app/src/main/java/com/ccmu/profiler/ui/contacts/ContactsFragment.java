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
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        ArrayList<Contact> contacts = new ArrayList<>();
        ContactAdapter contactsAdapter= new ContactAdapter(requireContext(), contacts);

        contactsList.setAdapter(contactsAdapter);

        if ((cursor != null ? cursor.getCount() : 0) > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex((ContactsContract.Contacts.DISPLAY_NAME)));
                ArrayList<String> number = new ArrayList<>();
                if (cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER) > 0)  {
                    Cursor cur = requireContext().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone._ID+" = ?",
                        new String[]{id},
                        null);
                    while (cur.moveToNext())
                        number.add(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    if (cur != null)
                        cur.close();
                }
                contactsAdapter.add(new Contact(id, name, number.toArray(new String[0])));
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