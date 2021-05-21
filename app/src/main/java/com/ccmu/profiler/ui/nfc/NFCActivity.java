package com.ccmu.profiler.ui.nfc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ccmu.profiler.R;
import com.ccmu.profiler.ui.contacts.Contact;
import com.ccmu.profiler.ui.contacts.ContactsFragment;
import com.ccmu.profiler.ui.home.HomeFragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NFCActivity extends Activity {

    public static final String ID_INTENT_DATA = "mode";
    public static final String NFC_WRITE_MODE = "write";
    public static final String NFC_READ_MODE = "read";

    private static final String CONTACT_INFO_DELIMITER_NFC_FORMAT = "~";
    private static final int ADD_CONTACT_REQUEST_CODE = 10;

    private NfcAdapter nfcAdapter;
    private Tag nfcTag;
    private PendingIntent pendingIntent;
    private IntentFilter[] writeTagFilters;

    private static boolean nfcDisabled = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nfc_activity);

        final Context context = this;

        String mode = getIntent().getStringExtra(ID_INTENT_DATA);

        initializeNFC();

        if (mode != null)
            switch (mode) {
                case NFC_WRITE_MODE:
                    try {
                        sendContactNFC(getIntent().getStringExtra(ContactsFragment.CONTACT_NAME_INTENT_ID),
                                getIntent().getStringExtra(ContactsFragment.CONTACT_SURNAME_INTENT_ID),
                                getIntent().getStringArrayExtra(ContactsFragment.CONTACT_NUMBERS_INTENT_ID));
                    } catch (IOException e) {
                        Toast.makeText(this, R.string.nfc_writing_IO_exception, Toast.LENGTH_LONG)
                                .show();
                    } catch (FormatException e) {
                        Toast.makeText(this, R.string.nfc_writing_format_exception, Toast.LENGTH_LONG)
                                .show();
                    }
                    break;
                case NFC_READ_MODE:
                    readFromIntent(getIntent());
                    pendingIntent = PendingIntent.getActivity(this, 0, new Intent(
                            this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                    IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                    tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
                    writeTagFilters = new IntentFilter[]{tagDetected};
                    // TODO: Display waiting animation until data recived
                    break;
                default:
                    // TODO: Send an error message
                    break;
            }
        else
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_alert_error_nfc_mode)
                    .setMessage(R.string.message_alert_error_nfc_mode)
                    .setIcon(R.drawable.ic_error)
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //onBackPressed();  // Sufficient?
                            startActivity(new Intent(context, HomeFragment.class));
                        }
                    })
                    .show();
    }

    private void initializeNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC", Toast.LENGTH_LONG).show();
            nfcDisabled = true;
            finish();
        }
    }

    private void sendContactNFC(String name, String surname, @Nullable String[] numbers) throws IOException, FormatException {
        StringBuilder formattedMessage = new StringBuilder();
        formattedMessage.append(name)
                .append(CONTACT_INFO_DELIMITER_NFC_FORMAT)
                .append(surname);
        if (numbers != null) {
            for (String s : numbers)
                formattedMessage.append(CONTACT_INFO_DELIMITER_NFC_FORMAT).append(s);
        }

        NdefRecord[] records = {createRecord(formattedMessage.toString())};
        NdefMessage message = new NdefMessage(records);
        if (nfcTag == null)
            return; // TODO: Display an error message?
        Ndef ndef = Ndef.get(nfcTag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }

    private NdefRecord createRecord(String text) {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes(StandardCharsets.US_ASCII);
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];

        payload[0] = (byte) langLength;
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] messages = null;
            if (rawMessages != null) {
                messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++)
                    messages[i] = (NdefMessage) rawMessages[i];
            }
            createNewContact(parseReceivedMessage(messages));
        }
    }

    private Contact parseReceivedMessage(@Nullable NdefMessage[] messages) {
        if (messages == null || messages.length == 0) return null;

        byte[] payload = messages[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;
        String text = null;
        try {
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }
        if (text == null) return null;
        String[] tokens = text.split(CONTACT_INFO_DELIMITER_NFC_FORMAT);
        String name = tokens[0];
        String surname = tokens[1];
        String[] numbers = new String[tokens.length - 3];
        System.arraycopy(tokens, 2, numbers, 0, tokens.length - 2);
        return new Contact(name, surname, numbers);
    }

    private void createNewContact(Contact contact) {
        if (contact == null) return;

        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);

        intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, contact.getFullName());

        ArrayList<ContentValues> data = new ArrayList<>();

        for (String s : contact.getNumbers()) {
            ContentValues row = new ContentValues();
            row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, s);
            row.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            data.add(row);
        }

        intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);

        startActivity(intent);
        // Since user will decide to add or edit a contact, a result is no longer needed
        //startActivityForResult(intent, ADD_CONTACT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_CONTACT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Contact has benn added", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeFragment.class));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeFragment.class));
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        readFromIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
            nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        writeModeOff();
    }

    @Override
    protected void onResume() {
        super.onResume();
        writeModeOn();
    }

    private void writeModeOn() {
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void writeModeOff() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    public static boolean isNFCDisabled() {
        return nfcDisabled;
    }

}
