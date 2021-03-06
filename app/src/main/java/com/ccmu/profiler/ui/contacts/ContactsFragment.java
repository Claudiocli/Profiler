package com.ccmu.profiler.ui.contacts;

import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ccmu.profiler.R;
import com.ccmu.profiler.gesture.OnSwipeTouchListener;
import com.ccmu.profiler.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private static final int REQUEST_CODE_CONTACTS_READ = 1;
    private static final int REQUEST_CODE_CALL_READ = 2;

    public static final String CONTACT_NAME_INTENT_ID = "contact_name_intent_id";
    public static final String CONTACT_SURNAME_INTENT_ID = "contact_surname_intent_id";
    public static final String CONTACT_NUMBERS_INTENT_ID = "contact_number_intent_id";
    public static final String ORDER_BY_FIRST_NAME = "order_by_first_name";
    public static final String ORDER_BY_LAST_NAME = "order_by_last_name";

    private ListView contactsList;
    private ListView favouriteContactsList;
    private String defaultOrder = ORDER_BY_FIRST_NAME;

    @SuppressLint("StaticFieldLeak")
    private static ContactsFragment instance = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);

        contactsList = root.findViewById(R.id.contactsList);
        favouriteContactsList = root.findViewById(R.id.favouriteContactsList);

        if (requireContext().checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_DENIED)
            requireActivity().requestPermissions(new String[]{READ_CONTACTS}, REQUEST_CODE_CONTACTS_READ);
        else
            doShowContacts();

        if (requireContext().checkSelfPermission(READ_CALL_LOG) == PackageManager.PERMISSION_DENIED)
            requireActivity().requestPermissions(new String[]{READ_CALL_LOG}, REQUEST_CODE_CONTACTS_READ);
        else
            getFavContacts();

        contactsList.setOnTouchListener(new OnSwipeTouchListener(contactsList));
        contactsList.setOnItemClickListener((parent, view, position, id) -> {
            LayoutInflater layoutInflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.popup_info_contact, null);

            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

            TextView nameTextView =
                    popupView.findViewById(R.id.popup_contact_info_name);
            //noinspection StringBufferReplaceableByString
            nameTextView.setText(
                    new StringBuilder().append(getResources().getString(R.string.name_placeholder))
                            .append(": ")
                            .append(((ContactModel) parent.getItemAtPosition(position))
                                    .getOnlyName()).toString());
            nameTextView.setTextSize(35);

            TextView surnameTextView = popupView.findViewById(R.id.popup_contact_info_surname);
            //noinspection StringBufferReplaceableByString
            surnameTextView.setText(
                    new StringBuilder().append(getResources().getString(R.string.surname_placeholder))
                            .append(": ")
                            .append(((ContactModel) parent.getItemAtPosition(position))
                                    .getOnlySurname()).toString());
            surnameTextView.setTextSize(35);

            LinearLayout linearLayout = popupView.findViewById(R.id.linear_layout_popup_contact_info);
            String[] numbers = ((ContactModel) parent.getItemAtPosition(position)).getNumbers();
            if (numbers.length != 0) {
                TextView numbersTextView = new TextView(popupView.getContext());
                if (numbers.length > 1) {
                    numbersTextView.setText("Numbers: ");
                    for (String number : numbers)
                        numbersTextView.append("\n" + number);
                } else
                    numbersTextView.setText("Number: " + numbers[0]);

                numbersTextView.setTextSize(35);
                numbersTextView.setGravity(Gravity.CENTER);
                linearLayout.addView(numbersTextView);
            }

            GradientDrawable border = new GradientDrawable();
            border.setColor(0xFFFFFFFF);
            int borderStroke = 8;
            border.setStroke(borderStroke, 0xFF000000);
            linearLayout.setBackground(border);
            linearLayout.setPadding(borderStroke * 2, borderStroke * 2, borderStroke * 2, borderStroke * 2);

            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            popupView.setOnTouchListener((v, event) -> {
                popupWindow.dismiss();
                return true;
            });
        });

        favouriteContactsList.setOnTouchListener(new OnSwipeTouchListener(favouriteContactsList));
        favouriteContactsList.setOnItemClickListener((parent, view, position, id) -> {
            LayoutInflater layoutInflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.popup_info_contact, null);

            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

            TextView nameTextView =
                    popupView.findViewById(R.id.popup_contact_info_name);
            //noinspection StringBufferReplaceableByString
            nameTextView.setText(
                    new StringBuilder().append(getResources().getString(R.string.name_placeholder))
                            .append(": ")
                            .append(((ContactModel) parent.getItemAtPosition(position))
                                    .getOnlyName()).toString());
            nameTextView.setTextSize(35);

            TextView surnameTextView = popupView.findViewById(R.id.popup_contact_info_surname);
            //noinspection StringBufferReplaceableByString
            surnameTextView.setText(
                    new StringBuilder().append(getResources().getString(R.string.surname_placeholder))
                            .append(": ")
                            .append(((ContactModel) parent.getItemAtPosition(position))
                                    .getOnlySurname()).toString());
            surnameTextView.setTextSize(35);

            LinearLayout linearLayout = popupView.findViewById(R.id.linear_layout_popup_contact_info);
            String[] numbers = ((ContactModel) parent.getItemAtPosition(position)).getNumbers();
            if (numbers.length != 0) {
                TextView numbersTextView = new TextView(popupView.getContext());
                if (numbers.length > 1) {
                    numbersTextView.setText("Numbers: ");
                    for (String number : numbers)
                        numbersTextView.append("\n" + number);
                } else
                    numbersTextView.setText("Number: " + numbers[0]);

                numbersTextView.setTextSize(35);
                numbersTextView.setGravity(Gravity.CENTER);
                linearLayout.addView(numbersTextView);
            }
            linearLayout.addView(new Button(linearLayout.getContext()));

            GradientDrawable border = new GradientDrawable();
            border.setColor(0xFFFFFFFF);
            int borderStroke = 8;
            border.setStroke(borderStroke, 0xFF000000);
            linearLayout.setBackground(border);
            linearLayout.setPadding(borderStroke * 2, borderStroke * 2, borderStroke * 2, borderStroke * 2);

            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            popupView.setOnTouchListener((v, event) -> {
                popupWindow.dismiss();
                return true;
            });
        });

        registerForContextMenu(contactsList);
        registerForContextMenu(favouriteContactsList);

        return root;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ListView lv = (ListView) acmi.targetView.getParent();

        if (item.getTitle().equals(getString(R.string.delete))) {
            ContactModel contact = (ContactModel) lv.getItemAtPosition(acmi.position);

            ContentResolver cr = requireContext().getContentResolver();
            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contact.getNumbers()[0]));
            @SuppressLint("Recycle") Cursor cursor = cr.query(contactUri, null, null, null, null);

            while (cursor.moveToNext()) {
                try {
                    if (!cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)).equals(contact.getId()))
                        continue;
                    String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);

                    cr.delete(uri, null, null);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
                }
            }
            doShowContacts();
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.contacts_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.contacts_settings) {
            startActivity(new Intent(getContext(), ContactSettings.class));
            return true;
        }
        if (item.getItemId() == R.id.add_contact) {
            startActivity(new Intent(getContext(), AddContactActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doShowContacts() {
        if (contactsList.getAdapter() != null)
            ((ContactAdapter) contactsList.getAdapter()).clear();
        ContentResolver cr = requireContext().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER},
                null,
                null,
                null);

        ArrayList<ContactModel> contactModels = new ArrayList<>();
        ContactAdapter contactsAdapter = new ContactAdapter(requireContext(), contactModels);

        contactsList.setAdapter(contactsAdapter);

        if ((cursor != null ? cursor.getCount() : 0) > 0) {
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String name = cursor.getString(cursor.getColumnIndex((ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                ArrayList<String> numberArray = new ArrayList<>();
                if (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER) > 0) {
                    Cursor cursor1 = requireContext().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);
                    if (cursor1 != null)
                        while (cursor1.moveToNext())
                            numberArray.add(cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    if (cursor1 != null)
                        cursor1.close();
                }
                contactsAdapter.add(new ContactModel(contactId, name, numberArray.toArray(new String[0])));
            }
        }
        if (defaultOrder.equals(ORDER_BY_LAST_NAME)) {
            contactsAdapter.sort((o1, o2) -> o1.getOnlySurname().compareTo(o2.getOnlySurname()));
            Log.d("ContactsSort", "sorted by last name");
        } else {
            contactsAdapter.sort((o1, o2) -> o1.getOnlyName().compareTo(o2.getOnlyName()));
            Log.d("ContactsSort", "sorted by first name");
        }

        if (cursor != null)
            cursor.close();
    }

    private void getFavContacts() {

        if (favouriteContactsList.getAdapter() != null)
            ((ContactAdapter) favouriteContactsList.getAdapter()).clear();

        List<String> numberMostInteractedWith = new ArrayList<>();
        List<ContactModel> contactsMostInteractedWith = new ArrayList<>();

        ContactAdapter contactsFavAdapter = new ContactAdapter(requireContext(), contactsMostInteractedWith);

        favouriteContactsList.setAdapter(contactsFavAdapter);

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.DATE
        };

        String sortOrder = String.format("%s limit 100 ", CallLog.Calls.DATE + " DESC");

        Cursor cursor = requireContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, sortOrder);

        while (cursor.moveToNext()) {
            String phoneNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));

            if (phoneNumber == null || numberMostInteractedWith.contains(phoneNumber))
                continue;

            numberMostInteractedWith.add(phoneNumber);
        }

        for (String number : numberMostInteractedWith) {
            Cursor contactLookup = requireContext().getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, number), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
            int phoneContactId = -1;
            String name = null;

            while (contactLookup.moveToNext()) {
                phoneContactId = contactLookup.getInt(contactLookup.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                name = contactLookup.getString(contactLookup.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
            contactLookup.close();

            if (phoneContactId == -1 || name == null)
                continue;

            String fomrattedNumber = "(" + number.substring(0, 3) + ") " + number.substring(3, 6) + "-" + number.substring(6);
            contactsMostInteractedWith.add(new ContactModel(String.valueOf(phoneContactId), name, new String[]{fomrattedNumber}));
        }

        if (defaultOrder.equals(ORDER_BY_LAST_NAME)) {
            contactsFavAdapter.sort((o1, o2) -> o1.getOnlySurname().compareTo(o2.getOnlySurname()));
            Log.d("ContactsSort", "Favourite contacts sorted by last name");
        } else {
            contactsFavAdapter.sort((o1, o2) -> o1.getOnlyName().compareTo(o2.getOnlyName()));
            Log.d("ContactsSort", "Favourite contacts sorted by first name");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requireContext().checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_DENIED)
            requireActivity().requestPermissions(new String[]{READ_CONTACTS}, REQUEST_CODE_CONTACTS_READ);
        else
            doShowContacts();
        if (requireContext().checkSelfPermission(READ_CALL_LOG) == PackageManager.PERMISSION_DENIED)
            requireActivity().requestPermissions(new String[]{READ_CALL_LOG}, REQUEST_CODE_CONTACTS_READ);
        else
            getFavContacts();
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
                        .setNeutralButton(R.string.ok, (dialog, which) -> getParentFragmentManager().beginTransaction()
                                .replace(R.id.navigation_home, new HomeFragment())
                                .commit())
                        .setIcon(R.drawable.ic_error)
                        .show();
            }
        }
        if (requestCode == REQUEST_CODE_CALL_READ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFavContacts();
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_alert_no_permission)
                        .setMessage(R.string.message_alert_no_permission)
                        .setNeutralButton(R.string.ok, (dialog, which) -> getParentFragmentManager().beginTransaction()
                                .replace(R.id.navigation_home, new HomeFragment())
                                .commit())
                        .setIcon(R.drawable.ic_error)
                        .show();
            }
        }
    }

    private static ContactsFragment getInstance() {
        return instance;
    }

    public static void changeDefaultOrder(String order) {
        ContactsFragment.getInstance().defaultOrder = order;
        ContactsFragment.getInstance().doShowContacts();
    }

}