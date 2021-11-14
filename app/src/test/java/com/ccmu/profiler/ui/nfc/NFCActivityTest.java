package com.ccmu.profiler.ui.nfc;

import static org.mockito.Mockito.mock;

import android.content.Intent;
import android.nfc.FormatException;
import android.util.Log;
import android.widget.Toast;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@PrepareForTest({Toast.class})
class NFCActivityTest {

    private NFCActivity nfc;

    @BeforeAll
    static void beforeSetUp() {
        try {
            Method m = Log.class.getDeclaredMethod("d", String.class, String.class);

            String[] logMethods = {"v", "d", "i", "e"};

            InvocationHandler syso = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    StringBuilder mesageBuilder = new StringBuilder();
                    for (Object o : args) {
                        String arg = o.toString();
                        mesageBuilder.append(arg);
                    }
                    System.out.println(mesageBuilder);
                    return mesageBuilder.toString().length();
                }
            };

            for (String logMethod : logMethods) {
                PowerMockito.replace(PowerMockito.method(Log.class, logMethod, String.class, String.class)).with(syso);
                PowerMockito.replace(PowerMockito.method(Log.class, logMethod, String.class, String.class, Throwable.class)).with(syso);
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @BeforeEach
    void setUp() {
        nfc = new NFCActivity();
    }

    @AfterEach
    void tearDown() {
        nfc = null;
    }

    @Test
    void onNewIntent_SendContact() {
        // Send Contact
        Intent intent = mock(Intent.class);
        intent.putExtra(NFCActivity.MODE, NFCActivity.NFC_WRITE_MODE);

        try {
            nfc.onNewIntent(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void onNewIntent_RetrieveContact() {
        // Retrieve Contact
        Intent intent = mock(Intent.class);
        intent.putExtra(NFCActivity.MODE, NFCActivity.NFC_WRITE_MODE);
        try {
            nfc.onNewIntent(intent);
/*
            Cursor cursor = nfc.getApplicationContext().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER},
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                    new String[] {"Claudio Lucisano"},
                    null);

            Assertions.assertNotNull(cursor);
            Assertions.assertTrue(cursor.getCount() > 0);
            Assertions.assertEquals(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)), "Claudio Lucisano");
*/
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    void sendContactTest() {
        try {
            nfc.sendContactNFC("Claudio", "Lucisano", new String[]{"3319969004"});
        } catch (IOException | FormatException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void readFromIntentTest() {
        Intent intent = mock(Intent.class);
        PowerMockito.when(intent.getAction()).thenReturn(null);

        /*StringBuilder formattedMessage = new StringBuilder();
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

        final int TECH_NDEF = 6;
        final String EXTRA_NDEF_MSG = "ndefmsg";              // NdefMessage (Parcelable)
        final String EXTRA_NDEF_MAXLENGTH = "ndefmaxlength";  // int (result for getMaxSize())
        final String EXTRA_NDEF_CARDSTATE = "ndefcardstate";  // int (1: read-only, 2: read/write, 3: unknown)
        final String EXTRA_NDEF_TYPE = "ndeftype";            // int (1: T1T, 2: T2T, 3: T3T, 4: T4T, 101: MF Classic, 102: ICODE)

        Bundle ndefBundle = mock(Bundle.class);
        ndefBundle.putInt(EXTRA_NDEF_MAXLENGTH, 48); // maximum message length: 48 bytes
        ndefBundle.putInt(EXTRA_NDEF_CARDSTATE, 1); // read-only
        ndefBundle.putInt(EXTRA_NDEF_TYPE, 2); // Type 2 tag
        ndefBundle.putParcelable(EXTRA_NDEF_MSG, message);*/

        //PowerMockito.when(intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)).thenReturn(ndefBundle.getParcelableArray(NfcAdapter.EXTRA_NDEF_MESSAGES));
        nfc.readFromIntent(intent);
    }

}