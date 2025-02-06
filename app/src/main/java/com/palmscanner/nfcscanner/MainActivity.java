package com.palmscanner.nfcscanner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jiebao.nfc.uartnfc.CardReaderDevice;
import com.jiebao.util.CardReaderUtils;
import com.jiebao.util.L;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextView tvShowInfo;
    private Button btnCardId;
    private Button btnReadBlock;
    private Button btnWriteBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvShowInfo = findViewById(R.id.show_info);

        CardReaderDevice.getInstance().initCardReader();
        L.setDebug(true);
        appendLog("Card reader init and set debug True");

        btnCardId = findViewById(R.id.btn_getCardid);
        btnCardId.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                Date date = new Date();
                String fwVersion = CardReaderDevice.getInstance().getNFCHWVersion();
                String cardNo = CardReaderDevice.getInstance().readCardNo();

                appendLog("Time: " + dateFormat.format(date) + ", NFC Firmware version: " + fwVersion + ", Card number: " + cardNo);
            }
        });

        btnReadBlock = findViewById(R.id.btn_readBlock);
        btnReadBlock.setOnClickListener(v -> {
            final byte[] KEY_READ = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
            String str = "";
            for (int i = 0; i < 64; i++) {
                byte[] bReturn = CardReaderDevice.getInstance().readM1CardChunkData(0, i, KEY_READ);
                if (bReturn != null) {
                    Log.e(TAG, "bReturn " + i + " length " + bReturn.length + " " + CardReaderUtils.byteArray2HexString(bReturn));
                    str += CardReaderUtils.byteArray2HexString(bReturn) + "\n";
                }
            }
            appendLog("M1 card read: " + str);
        });

        btnWriteBlock = findViewById(R.id.btn_writeBlock);
        btnWriteBlock.setOnClickListener(v -> {

            final byte[] KEY_READ = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
            final byte[] DATA_WRITE = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

            CardReaderDevice.getInstance().writeM1CardChunkData(0, 4, KEY_READ, DATA_WRITE);
            appendLog("M1 card data updated.");
        });

        findViewById(R.id.btn_enable_reader).setOnClickListener(this);
        findViewById(R.id.btn_disable_reader).setOnClickListener(this);
        findViewById(R.id.btn_getBankCardid).setOnClickListener(this);
        findViewById(R.id.btn_get_nfc_hw_version).setOnClickListener(this);
    }


    private void appendLog(String log) {
        Log.d("(NFCScanner)", log);
        if (this.tvShowInfo != null) {
            String text = (String) tvShowInfo.getText();
            this.tvShowInfo.setText(String.format("%s\n%s", text, log));
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        CardReaderDevice.getInstance().deInitCardReader();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_enable_reader) {
            CardReaderDevice.getInstance().initCardReader();
            appendLog("Card reader init..");
        } else if (v.getId() == R.id.btn_disable_reader) {
            CardReaderDevice.getInstance().deInitCardReader();
            appendLog("Card reader deInit..");
        } else if (v.getId() == R.id.btn_getBankCardid) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = new Date();
            String fwVersion = CardReaderDevice.getInstance().getNFCHWVersion();
            String cardNo = CardReaderDevice.getInstance().readBankCardNo();
            appendLog("Time: " + dateFormat.format(date) + ", NFC Firmware version: " + fwVersion + ", Bank Card number: " + cardNo);
        } else if (v.getId() == R.id.btn_get_nfc_hw_version) {
            String fwVersion = CardReaderDevice.getInstance().getNFCHWVersion();
            appendLog(String.format("NFC Firmware version: %s", fwVersion));
        }
    }
}