package com.ccmu.profiler.ui.nfc;

import android.content.Intent;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NFCActivityTest {

    private ActivityScenario<NFCActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(NFCActivity.class);
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void onCreate_EnsureNFCIsEnabled() {
        Lifecycle.State finalState = scenario.getState();
        Assert.assertEquals(Lifecycle.State.RESUMED, finalState);
    }

    @Test
    public void onNewIntent_thenSentAndReadContact() {
        // Send my contact
        scenario.onActivity(activity -> {
            Class<?> tagClass = Tag.class;
            Class<?> nfcClass = NFCActivity.class;

            Intent intent = new Intent(activity, NFCActivity.class);
            intent.putExtra(NFCActivity.MODE, NFCActivity.NFC_WRITE_MODE);
            try {
                Method onNewIntentMethod = nfcClass.getDeclaredMethod("onNewIntent", Intent.class);
                // There is no `createMockTag()` method anymore TODO: find a workaround
                Method createMockTagMethod = tagClass.getDeclaredMethod("createMockTag", byte[].class, int[].class, Bundle[].class);

                Field nfcTagField = nfcClass.getDeclaredField("nfcTag");

                final int TECH_NFC_A = 1;
                final String EXTRA_NFC_A_SAK = "sak";    // short (SAK byte value)
                final String EXTRA_NFC_A_ATQA = "atqa";  // byte[2] (ATQA value)

                final int TECH_NDEF = 6;
                final String EXTRA_NDEF_MAXLENGTH = "ndefmaxlength";  // int (result for getMaxSize())
                final String EXTRA_NDEF_CARDSTATE = "ndefcardstate";  // int (1: read-only, 2: read/write, 3: unknown)
                final String EXTRA_NDEF_TYPE = "ndeftype";            // int (1: T1T, 2: T2T, 3: T3T, 4: T4T, 101: MF Classic, 102: ICODE)

                Bundle nfcaBundle = new Bundle();
                nfcaBundle.putByteArray(EXTRA_NFC_A_ATQA, new byte[]{(byte) 0x44, (byte) 0x00}); //ATQA for Type 2 tag
                nfcaBundle.putShort(EXTRA_NFC_A_SAK, (short) 0x00); //SAK for Type 2 tag

                Bundle ndefBundle = new Bundle();
                ndefBundle.putInt(EXTRA_NDEF_MAXLENGTH, 48); // maximum message length: 48 bytes
                ndefBundle.putInt(EXTRA_NDEF_CARDSTATE, 1); // read-only
                ndefBundle.putInt(EXTRA_NDEF_TYPE, 2); // Type 2 tag

                byte[] tagId = new byte[]{(byte) 0x3F, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xAB};

                Tag mockTag = (Tag) createMockTagMethod.invoke(null, tagId, new int[]{TECH_NFC_A, TECH_NDEF}, new Bundle[]{nfcaBundle, ndefBundle});

                nfcTagField.set(nfcClass, mockTag);

                onNewIntentMethod.invoke(nfcClass, intent);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
                e.printStackTrace();
                Assert.fail();
            }
            // TODO: check and assert
        });
        // Receive contact
        scenario.onActivity(activity -> {
            Class<?> tagClass = Tag.class;
            Class<?> nfcClass = NFCActivity.class;

            Intent intent = new Intent(activity, NFCActivity.class);
            intent.putExtra(NFCActivity.MODE, NFCActivity.NFC_READ_MODE);
            try {
                Method onNewIntentMethod = nfcClass.getDeclaredMethod("onNewIntent", Intent.class);
                // There is no `createMockTag()` method anymore TODO: find a workaround
                Method createMockTagMethod = tagClass.getDeclaredMethod("createMockTag", byte[].class, int[].class, Bundle[].class);

                Field nfcTagField = nfcClass.getDeclaredField("nfcTag");

                final int TECH_NFC_A = 1;
                final String EXTRA_NFC_A_SAK = "sak";    // short (SAK byte value)
                final String EXTRA_NFC_A_ATQA = "atqa";  // byte[2] (ATQA value)

                final int TECH_NDEF = 6;
                final String EXTRA_NDEF_MSG = "ndefmsg";              // NdefMessage (Parcelable)
                final String EXTRA_NDEF_MAXLENGTH = "ndefmaxlength";  // int (result for getMaxSize())
                final String EXTRA_NDEF_CARDSTATE = "ndefcardstate";  // int (1: read-only, 2: read/write, 3: unknown)
                final String EXTRA_NDEF_TYPE = "ndeftype";            // int (1: T1T, 2: T2T, 3: T3T, 4: T4T, 101: MF Classic, 102: ICODE)

                Bundle nfcaBundle = new Bundle();
                nfcaBundle.putByteArray(EXTRA_NFC_A_ATQA, new byte[]{(byte) 0x44, (byte) 0x00}); //ATQA for Type 2 tag
                nfcaBundle.putShort(EXTRA_NFC_A_SAK, (short) 0x00); //SAK for Type 2 tag

                Bundle ndefBundle = new Bundle();
                ndefBundle.putInt(EXTRA_NDEF_MAXLENGTH, 48); // maximum message length: 48 bytes
                ndefBundle.putInt(EXTRA_NDEF_CARDSTATE, 1); // read-only
                ndefBundle.putInt(EXTRA_NDEF_TYPE, 2); // Type 2 tag

                StringBuilder formattedMessage = new StringBuilder();
                String CONTACT_INFO_DELIMITER_NFC_FORMAT = "~";
                formattedMessage.append("Claudio")
                        .append(CONTACT_INFO_DELIMITER_NFC_FORMAT)
                        .append("Lucisano");
                formattedMessage.append(CONTACT_INFO_DELIMITER_NFC_FORMAT).append("3319969004");

                String lang = "en";
                byte[] textBytes = formattedMessage.toString().getBytes();
                byte[] langBytes = lang.getBytes(StandardCharsets.US_ASCII);
                int langLength = langBytes.length;
                int textLength = textBytes.length;
                byte[] payload = new byte[1 + langLength + textLength];

                payload[0] = (byte) langLength;
                System.arraycopy(langBytes, 0, payload, 1, langLength);
                System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

                NdefRecord records = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
                NdefMessage message = new NdefMessage(records);
                ndefBundle.putParcelable(NfcAdapter.EXTRA_NDEF_MESSAGES, message);

                byte[] tagId = new byte[]{(byte) 0x3F, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xAB};

                Tag mockTag = (Tag) createMockTagMethod.invoke(null, tagId, new int[]{TECH_NFC_A, TECH_NDEF}, new Bundle[]{nfcaBundle, ndefBundle});

                nfcTagField.set(nfcClass, mockTag);

                onNewIntentMethod.invoke(nfcClass, intent);

                Cursor cursor = activity.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER},
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                        new String[]{"Claudio Lucisano"},
                        null);

                Assert.assertNotNull(cursor);
                Assert.assertTrue(cursor.getCount() > 0);
                Assert.assertEquals(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)), "Claudio Lucisano");

            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void onPause() {
        Lifecycle.State finalState = scenario.getState();
        Assert.assertEquals(Lifecycle.State.RESUMED, finalState);
    }

    @Test
    public void onResume() {
        Lifecycle.State finalState = scenario.getState();
        Assert.assertEquals(Lifecycle.State.RESUMED, finalState);
    }

    @Test
    public void isNFCDisabled() {
        try {
            Assert.assertTrue(NFCActivity.class.getDeclaredField("nfcDisabled").getBoolean(NFCActivity.class));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}